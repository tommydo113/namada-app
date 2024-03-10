package com.namada.app.ui.proposal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.namada.app.domain.Proposal
import com.namada.app.network.AppNetwork2
import com.namada.app.network.asProposalModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProposalViewModel(application: Application) : AndroidViewModel(application){
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    private val _proposals = MutableLiveData<List<Proposal>>()
    val proposals: LiveData<List<Proposal>> = _proposals
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    init {
        getProposalsFromApi()
    }

    private fun getProposalsFromApi() {
        _isRefreshing.postValue(true)
        uiScope.launch {
            try {
                val proposalList = AppNetwork2.appService.getProposalList().proposals.asProposalModel()
                    .sortedByDescending { item -> item.id }
//                println("proposalList $proposalList")
                _isRefreshing.postValue(false)
                _proposals.postValue(proposalList)
            }catch (e: Exception){
                e.printStackTrace()
                _isRefreshing.postValue(false)
            }
        }
    }
    fun refresh() {
        getProposalsFromApi()
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}