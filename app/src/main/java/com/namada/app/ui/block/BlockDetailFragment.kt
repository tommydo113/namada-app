package com.namada.app.ui.block

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.namada.app.R
import com.namada.app.databinding.FragmentBlockDetailBinding
import com.namada.app.ui.tx.TransactionAdapter
import com.namada.app.ui.tx.TransactionClick
import com.namada.app.util.setActionBarTitle

/**
 * A simple [Fragment] subclass.
 * Use the [BlockDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlockDetailFragment : Fragment() {
    val args: BlockDetailFragmentArgs by navArgs()
    private var _binding: FragmentBlockDetailBinding? = null
    private val binding get() = _binding!!
    private var viewModelAdapter: TransactionAdapter? = null

    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onActivityCreated, which we
     * do in this Fragment.
     */
    private val viewModel: BlockDetailViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this)[BlockDetailViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBlockDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val root: View = binding.root
        viewModelAdapter = TransactionAdapter(TransactionClick {
            println("click tx")
        })
        root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val block = args.block
        viewModel.block = block
        println("block $block")
        view.findViewById<TextView>(R.id.block_height).apply {
            text = buildString { append(" " + block.height) }
        }
        view.findViewById<TextView>(R.id.block_hash).apply {
            text = "Hash: " + block.hash

        }
        view.findViewById<TextView>(R.id.proposer).apply {
            text = buildString {
                append("Proposer: ")
                append(block.proposerMoniker)
                append("\n")
                append(block.proposerAddress)
            }
        }
        view.findViewById<TextView>(R.id.time).apply {
            text = buildString { block.time }
        }
        view.findViewById<TextView>(R.id.tx_count).apply {
            text = "Transactions: " + block.txCount
        }
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactions?.apply {
                viewModelAdapter?.transactions = transactions
            }
        }
        viewModel.getTransactionOfBlockFromApi()

    }
    companion object {

        @JvmStatic
        fun newInstance() =
            BlockDetailFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}