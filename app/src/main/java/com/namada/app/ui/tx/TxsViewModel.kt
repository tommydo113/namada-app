package com.namada.app.ui.tx

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.namada.app.domain.Transaction
import com.namada.app.network.AppNetwork
import com.namada.app.network.AppNetwork3
import com.namada.app.network.asTransactionModel
import com.namada.app.network.toBlockModel
import com.namada.app.network.toTransactionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TxsViewModel : ViewModel() {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    private val _transaction = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transaction
    private var allTransactions: MutableList<Transaction> = emptyList<Transaction>().toMutableList()
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    private val _txSearchResult = MutableLiveData<Transaction?>()
    val txSearchResult: LiveData<Transaction?> = _txSearchResult

    init {
        getTransactionsFromApi()
    }

    private fun getTransactionsFromApi() {
        _isRefreshing.postValue(true)
        uiScope.launch {
            try {
                val transactions = AppNetwork.appService.getTransaction(10).asTransactionModel()
                allTransactions.clear()
                allTransactions.addAll(transactions)
                _isRefreshing.postValue(false)
                println("transactions: $transactions")
                _transaction.postValue(allTransactions)
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

    fun loadNextDataFromApi(page: Int) {
        val nextHeight = allTransactions.last().height -1
        if(nextHeight >=0){
            uiScope.launch {
                try {
                    val transactions = AppNetwork.appService.getNextTxList(nextHeight).asTransactionModel()
                    allTransactions.addAll(transactions)
                    _transaction.postValue(allTransactions)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    fun searchByTxHash(txHash: String) {
        _isRefreshing.postValue(true)
        uiScope.launch {
            try {
                val txSearch = AppNetwork3.appService.searchByTxHash(txHash, txHash).toTransactionModel()
                println("txSearch $txSearch")
                _isRefreshing.postValue(false)
                _txSearchResult.postValue(txSearch)
            }catch (e: Exception){
                e.printStackTrace()
                _isRefreshing.postValue(false)
            }
        }
    }

    fun clearTxSearchResult() {
        _txSearchResult.value = null
    }
}