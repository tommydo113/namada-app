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
    private var allValidators: List<Validator> = emptyList()
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    init {
        getValidatorsFromApi()
    }

    private fun getValidatorsFromApi() {
        _isRefreshing.postValue(true)
        uiScope.launch {
            try {
                allValidators = AppNetwork.appService.getValidatorList().validators.asValidatorModel()
//                println("validator $validators")
                _isRefreshing.postValue(false)
                _validators.postValue(allValidators)
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

    fun search(inputQuery: String) {
        val query = inputQuery.toLowerCase()
        val filterResult = allValidators.filter { validator ->
            validator.moniker.contains(query)
                    || validator.operatorAddress.contains(query)
                    || validator.hexAddress.contains(query)
        }.toTypedArray().toList()
        _validators.postValue(filterResult)
    }
}