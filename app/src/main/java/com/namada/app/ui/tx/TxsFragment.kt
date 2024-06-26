package com.namada.app.ui.tx

import android.app.ProgressDialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.namada.app.R
import com.namada.app.databinding.FragmentTransactionsBinding
import com.namada.app.databinding.TransactionItemBinding
import com.namada.app.domain.Transaction
import com.namada.app.ui.home.HomeFragmentDirections
import com.namada.app.util.EndlessRecyclerViewScrollListener
import com.namada.app.util.dismissKeyboard
import com.namada.app.util.isNumeric
import com.namada.app.util.isValidHashTx
import com.namada.app.util.setActionBarTitle

class TxsFragment : Fragment() {

    private lateinit var progressDialog: ProgressDialog
    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var swipeContainer: SwipeRefreshLayout
    private var viewModelAdapter: TransactionAdapter? = null
    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onActivityCreated, which we
     * do in this Fragment.
     */
    private val viewModel: TxsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this)[TxsViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val root: View = binding.root
        viewModelAdapter = TransactionAdapter(TransactionClick {
            println("click tx")
            findNavController().navigate(
                TxsFragmentDirections.actionNavigationTransactionToTxDetailFragment(it)
            )
        })
        root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
            addOnScrollListener(object : EndlessRecyclerViewScrollListener(layoutManager as LinearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    println("tx load more")
                    viewModel.loadNextDataFromApi(page)
                }
            })
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
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setCancelable(false)
        progressDialog?.setMessage("Loading data, please wait for a moment")
//        progressDialog?.show()
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactions?.apply {
                viewModelAdapter?.transactions = transactions
                setActionBarTitle(buildString {
                    append("Last Transactions")
                })
            }
//            progressDialog?.dismiss()
        }
        viewModel.isRefreshing.observe(viewLifecycleOwner){ isRefreshing ->
            swipeContainer.isRefreshing = isRefreshing
        }
        viewModel.txSearchResult.observe(viewLifecycleOwner){ transaction ->
            if(transaction != null){
                findNavController().navigate(
                    TxsFragmentDirections.actionNavigationTransactionToTxDetailFragment(transaction)
                )
                viewModel.clearTxSearchResult()
            }
        }
        initSearchInputListener()
    }

    private fun initSearchInputListener() {
        binding.input.setOnEditorActionListener { view: View, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(view)
                true
            } else {
                false
            }
        }
        binding.input.setOnKeyListener { view: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(view)
                true
            } else {
                false
            }
        }
    }

    private fun doSearch(v: View) {
        val query = binding.input.text.toString()
        // Dismiss keyboard
        dismissKeyboard(activity, v.windowToken)
        if(isValidHashTx(query)){
            Snackbar.make(v, "Format of Tx Hash is incorrect", Snackbar.LENGTH_SHORT).show()
        }else {
            viewModel.searchByTxHash(query)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


/**
 * RecyclerView Adapter for setting up data binding on the items in the list.
 */
class TransactionAdapter(val callback: TransactionClick) : RecyclerView.Adapter<TransactionViewHolder>() {

    var transactions: List<Transaction> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val withDataBinding: TransactionItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            TransactionViewHolder.LAYOUT,
            parent,
            false
        )
        return TransactionViewHolder(withDataBinding)
    }

    override fun getItemCount()= transactions.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.transaction = transactions[position]
            it.transactionCallback = callback
        }
        val item = transactions[position]
        holder.gasUsed.text = buildString {
            append("")
            append(item.gasUsed)
        }
        holder.blockHeight.text = buildString {
            append("")
            append(item.height)
        }
        if(item.status == 0){
            holder.status.text = "Success"
        }else holder.status.text = "Failed"

    }

}

class TransactionViewHolder(val viewDataBinding: TransactionItemBinding):
    RecyclerView.ViewHolder(viewDataBinding.root){
    val blockHeight: TextView = viewDataBinding.blockHeight
    val gasUsed: TextView = viewDataBinding.txGasUsed
    val status: TextView = viewDataBinding.status
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.transaction_item
    }
}

/**
 * Click listener for Blocks. By giving the block a name it helps a reader understand what it does.
 *
 */
class TransactionClick(val transaction: (Transaction) -> Unit) {
    /**
     * Called when a Transaction is clicked
     *
     * @param transaction the Transaction that was clicked
     */
    fun onClick(transaction: Transaction) = transaction(transaction)
}