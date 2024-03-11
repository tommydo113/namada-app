package com.namada.app.ui.proposal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.namada.app.R
import com.namada.app.databinding.FragmentProposalBinding
import com.namada.app.databinding.ProposalItemBinding
import com.namada.app.domain.Proposal
import com.namada.app.util.setActionBarTitle

class ProposalFragment : Fragment() {

    private var _binding: FragmentProposalBinding? = null
    private lateinit var swipeContainer: SwipeRefreshLayout
    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onActivityCreated, which we
     * do in this Fragment.
     */
    private val viewModel: ProposalViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this)[ProposalViewModel::class.java]
    }
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var viewModelAdapter: ProposalAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProposalBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val root: View = binding.root
        viewModelAdapter = ProposalAdapter(ProposalClick {
            println("click Proposal")
        })
        root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }
        swipeContainer = root.findViewById<SwipeRefreshLayout>(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            viewModel.refresh()
        }
        return root
    }

    /**
     * Called immediately after onCreateView() has returned, and fragment's
     * view hierarchy has been created. It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.proposals.observe(viewLifecycleOwner) { proposals ->
            proposals?.apply {
                viewModelAdapter?.proposals = proposals
                setActionBarTitle(buildString {
                    append("Proposals: ")
                        .append(proposals[0].id)
                })
            }
        }
        viewModel.isRefreshing.observe(viewLifecycleOwner){ isRefreshing ->
            swipeContainer.isRefreshing = isRefreshing
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * Click listener for Blocks. By giving the block a name it helps a reader understand what it does.
 *
 */
class ProposalClick(val proposal: (Proposal) -> Unit) {
    /**
     * Called when a Block is clicked
     *
     * @param Proposal the Block that was clicked
     */
    fun onClick(proposal: Proposal) = proposal(proposal)
}

/**
 * RecyclerView Adapter for setting up data binding on the items in the list.
 */
class ProposalAdapter(val callback: ProposalClick) : RecyclerView.Adapter<ProposalViewHolder>() {

    var proposals: List<Proposal> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProposalViewHolder {
        val withDataBinding: ProposalItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            ProposalViewHolder.LAYOUT,
            parent,
            false
        )
        return ProposalViewHolder(withDataBinding)
    }

    override fun getItemCount()= proposals.size

    override fun onBindViewHolder(holder: ProposalViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.proposal = proposals[position]
            it.proposalCallback = callback
        }
        val item = proposals[position]
        holder.id.text = buildString {
        append("")
        append(item.id)
    }
        holder.startEnd.text = buildString {
        append(item.startEpoch)
        append(" / ")
        append(item.endEpoch)
    }
        holder.yesNo.text = buildString {
        append(item.yayVotes)
        append(" / ")
        append(item.nayVotes)
    }
    }

}

/**
 * ViewHolder for Proposal items. All work is done by data binding.
 */
class ProposalViewHolder(val viewDataBinding: ProposalItemBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    val id:TextView = viewDataBinding.proposalId
    val startEnd = viewDataBinding.startEndEpoch
    val yesNo = viewDataBinding.yesNo
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.proposal_item
    }
}