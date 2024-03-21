package com.namada.app.ui.proposal

import android.app.ProgressDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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

class ProposalFragment : Fragment() , AdapterView.OnItemSelectedListener{

    private var _binding: FragmentProposalBinding? = null
    private lateinit var swipeContainer: SwipeRefreshLayout
    private var screenWidth = 1
    private var progressDialog: ProgressDialog? = null
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
        setupStatusSpinner(root)
        setupKindSpinner(root)
        getWindowWidth()

        viewModelAdapter = ProposalAdapter(screenWidth, ProposalClick {
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

    private fun setupStatusSpinner(root: View) {
        val spinner: Spinner = root.findViewById(R.id.spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.proposal_status,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this
    }

    private fun setupKindSpinner(root: View){
        val kindSpinner: Spinner = root.findViewById(R.id.kind_spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.proposal_kind,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            kindSpinner.adapter = adapter
        }
        kindSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val item = parent.getItemAtPosition(pos)
                println("item $item")
                viewModel.onKindSelected(item as String)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }

    private fun getWindowWidth() {
        // initializing variable for display metrics.
        val displayMetrics = DisplayMetrics()

        // on below line we are getting metrics
        // for display using window manager.
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        // on below line we are getting height
        // and width using display metrics.
        val height = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item is selected. You can retrieve the selected item using
        val item = parent.getItemAtPosition(pos)
        println("item $item")
        viewModel.onStatusSelected(item as String)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback.
    }

    /**
     * Called immediately after onCreateView() has returned, and fragment's
     * view hierarchy has been created. It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(requireContext())
        progressDialog?.setCancelable(false)
        progressDialog?.setMessage("Loading data, please wait for a moment")
        progressDialog?.show()
        viewModel.proposals.observe(viewLifecycleOwner) { proposals ->
            proposals?.apply {
                viewModelAdapter?.proposals = proposals
//                setActionBarTitle(buildString {
//                    append("Proposals: ")
//                        .append(proposals[0].id)
//                })
            }
            progressDialog?.dismiss()
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
class ProposalAdapter(val screenWidth: Int, val callback: ProposalClick) : RecyclerView.Adapter<ProposalViewHolder>() {

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
        val totalVote = item.yayVotes + item.nayVotes + item.abstainVotes
        if(totalVote > 0){
            holder.voteLayout.visibility = View.VISIBLE
            holder.yesTv.layoutParams.width = ((item.yayVotes * screenWidth)/totalVote).toInt()
            holder.noTv.layoutParams.width = ((item.nayVotes *  screenWidth)/totalVote).toInt()
            holder.abstainTv.layoutParams.width = ((item.abstainVotes *  screenWidth)/totalVote).toInt()
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
    val yesTv = viewDataBinding.yesTv
    val noTv = viewDataBinding.noTv
    val abstainTv = viewDataBinding.abstainTv
    val voteLayout = viewDataBinding.voteResultLayout
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.proposal_item
    }
}