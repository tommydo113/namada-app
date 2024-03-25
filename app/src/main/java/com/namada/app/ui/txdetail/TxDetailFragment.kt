package com.namada.app.ui.txdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.namada.app.R
import com.namada.app.databinding.FragmentTxDetailBinding
import com.namada.app.ui.tx.TransactionAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [TxDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TxDetailFragment : Fragment() {
    val args: TxDetailFragmentArgs by navArgs()
    private var _binding: FragmentTxDetailBinding? = null
    private val binding get() = _binding!!

    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onActivityCreated, which we
     * do in this Fragment.
     */
    private val viewModel: TxDetailViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this)[TxDetailViewModel::class.java]
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
        _binding = FragmentTxDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val root: View = binding.root

        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val transaction = args.transaction
        println("transaction $transaction")
        view.findViewById<TextView>(R.id.block_height).apply {
            text = transaction.height.toString()
        }
        view.findViewById<TextView>(R.id.txHash).apply {
            text = transaction.hash
        }
        view.findViewById<TextView>(R.id.status).apply {
            text = if (transaction.status == 0) "Status: Success" else "Status: Failed"
        }
        view.findViewById<TextView>(R.id.gas).apply {
            text = "Gas: " + transaction.gasWanted.toString() + "/" + transaction.gasUsed.toString()
        }


    }
    companion object {

        @JvmStatic
        fun newInstance() =
            TxDetailFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}