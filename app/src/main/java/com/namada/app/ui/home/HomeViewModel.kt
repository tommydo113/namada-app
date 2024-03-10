package com.namada.app.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.namada.app.domain.Block
import com.namada.app.network.AppNetwork
import com.namada.app.network.NetworkBlock
import com.namada.app.network.asDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.logging.Logger

class HomeViewModel(application: Application) : AndroidViewModel(application){

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    private val _blocks = MutableLiveData<List<Block>>()
    val blocks: LiveData<List<Block>> = _blocks

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    init {
        getBlocksFromApi()
    }

    private fun getBlocksFromApi() {
        _isRefreshing.postValue(true)
        uiScope.launch {
            try {
                val blocks = AppNetwork.appService.getBlocklist().asDomainModel()
                println("blocks: $blocks")
                _isRefreshing.postValue(false)
                _blocks.postValue(blocks)
            }catch (e: Exception){
                _isRefreshing.postValue(false)
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun refresh() {
        getBlocksFromApi()
    }
}