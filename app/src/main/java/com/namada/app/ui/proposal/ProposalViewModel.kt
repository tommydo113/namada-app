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
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.PlatformDependentDeclarationFilter.All

class ProposalViewModel(application: Application) : AndroidViewModel(application){
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    private var allProposals: MutableList<Proposal> = emptyList<Proposal>().toMutableList()
    private val _proposals = MutableLiveData<List<Proposal>>()
    val proposals: LiveData<List<Proposal>> = _proposals
    private val _isRefreshing = MutableLiveData<Boolean>()
    private var selectedStatus = "All"
    private var selectedKind = "All"
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
                allProposals.clear()
                allProposals.addAll(proposalList)
                _isRefreshing.postValue(false)
                _proposals.postValue(allProposals)
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

    fun onStatusSelected(status: String) {
        selectedStatus = status
        doFilter()
    }

    fun onKindSelected(kind: String) {
        selectedKind = kind
        doFilter()
    }

    private fun doFilter() {
        if (allProposals.isNotEmpty()) {
            if (selectedStatus == "All") {
                if (selectedKind == "All") {
                    _proposals.postValue(allProposals)
                } else {
                    _proposals.postValue(filterByKind(allProposals))
                }
            } else {
                if (selectedKind == "All") {
                    _proposals.postValue(filterByStatus(allProposals))
                } else {
                    _proposals.postValue(filterByKind(filterByStatus(allProposals)))
                }
            }
        }
    }

    private fun filterByStatus(input: List<Proposal>) = input
        .filter { proposal: Proposal -> proposal.result == selectedStatus }
        .toTypedArray().toList()

    private fun filterByKind(input: List<Proposal>) = input
        .filter { proposal: Proposal -> proposal.kind == selectedKind }
        .toTypedArray().toList()
}