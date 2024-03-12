package com.namada.app.ui.block

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.namada.app.domain.Block
import com.namada.app.domain.Transaction
import com.namada.app.network.AppNetwork
import com.namada.app.network.asTransactionModel
import com.namada.app.network.asValidatorModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BlockDetailViewModel(application: Application) : AndroidViewModel(application){
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    lateinit var block: Block
    private val _transaction = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transaction

     fun getTransactionOfBlockFromApi() {
         if(block.txCount == 0) return
        _isRefreshing.postValue(true)
        uiScope.launch {
            try {
                val transactions = AppNetwork.appService.getTransactionsOfBlock(block.height).asTransactionModel()
                _isRefreshing.postValue(false)
                _transaction.postValue(transactions)
            }catch (e: Exception){
                e.printStackTrace()
                _isRefreshing.postValue(false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}