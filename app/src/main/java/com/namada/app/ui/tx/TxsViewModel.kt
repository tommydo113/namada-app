package com.namada.app.ui.tx

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.namada.app.domain.Transaction
import com.namada.app.network.AppNetwork
import com.namada.app.network.asTransactionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TxsViewModel : ViewModel() {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    private val _transaction = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transaction
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    init {
        getTransactionsFromApi()
    }

    private fun getTransactionsFromApi() {
        _isRefreshing.postValue(true)
        uiScope.launch {
            try {
                val transactions = AppNetwork.appService.getTransaction(40).asTransactionModel()
                _isRefreshing.postValue(false)
                println("transactions: $transactions")
                _transaction.postValue(transactions)
            }catch (e: Exception){
                e.printStackTrace()
                _isRefreshing.postValue(false)
            }
        }
    }
    fun refresh() {
        getTransactionsFromApi()
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}