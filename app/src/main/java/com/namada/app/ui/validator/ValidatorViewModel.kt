package com.namada.app.ui.validator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.namada.app.domain.Validator
import com.namada.app.network.AppNetwork
import com.namada.app.network.asDomainModel
import com.namada.app.network.asValidatorModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ValidatorViewModel(application: Application) : AndroidViewModel(application){
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    private val _validators = MutableLiveData<List<Validator>>()
    val validators: LiveData<List<Validator>> = _validators
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    init {
        getValidatorsFromApi()
    }

    private fun getValidatorsFromApi() {
        _isRefreshing.postValue(true)
        uiScope.launch {
            try {
                val validators = AppNetwork.appService.getValidatorList().validators.asValidatorModel()
//                println("validator $validators")
                _isRefreshing.postValue(false)
                _validators.postValue(validators)
            }catch (e: Exception){
                e.printStackTrace()
                _isRefreshing.postValue(false)
            }
        }
    }
    fun refresh() {
        getValidatorsFromApi()
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}