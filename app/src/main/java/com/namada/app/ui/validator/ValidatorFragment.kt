package com.namada.app.ui.validator

import android.os.Bundle
import android.text.TextUtils
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
import com.namada.app.databinding.FragmentValidatorBinding
import com.namada.app.databinding.ValidatorItemBinding
import com.namada.app.domain.Validator
import org.w3c.dom.Text

class ValidatorFragment : Fragment() {

    private var _binding: FragmentValidatorBinding? = null
    private lateinit var swipeContainer: SwipeRefreshLayout
    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onActivityCreated, which we
     * do in this Fragment.
     */
    private val viewModel: ValidatorViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this)[ValidatorViewModel::class.java]
    }
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var viewModelAdapter: ValidatorAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentValidatorBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val root: View = binding.root
        viewModelAdapter = ValidatorAdapter(ValidatorClick {
            println("click validator")
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
        viewModel.validators.observe(viewLifecycleOwner) { validators ->
            validators?.apply {
                viewModelAdapter?.validators = validators
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
class ValidatorClick(val validator: (Validator) -> Unit) {
    /**
     * Called when a Block is clicked
     *
     * @param validator the Block that was clicked
     */
    fun onClick(validator: Validator) = validator(validator)
}

/**
 * RecyclerView Adapter for setting up data binding on the items in the list.
 */
class ValidatorAdapter(val callback: ValidatorClick) : RecyclerView.Adapter<ValidatorViewHolder>() {

    var validators: List<Validator> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValidatorViewHolder {
        val withDataBinding: ValidatorItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            ValidatorViewHolder.LAYOUT,
            parent,
            false
        )
        return ValidatorViewHolder(withDataBinding)
    }

    override fun getItemCount()= validators.size

    override fun onBindViewHolder(holder: ValidatorViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.validator = validators[position]
            it.validatorCallback = callback
        }
        val item = validators[position]
        holder.moniker.text = "${item.moniker} - Rank:${item.rank}"
        holder.votingPower.text = item.tokens.toString()
        holder.votingPowerPercent.text = item.votingPercentage.toString()
        if(TextUtils.isEmpty(item.operatorAddress)) holder.address.visibility = View.GONE
    }

}

/**
 * ViewHolder for Validator items. All work is done by data binding.
 */
class ValidatorViewHolder(val viewDataBinding: ValidatorItemBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    val moniker: TextView = viewDataBinding.moniker
    val votingPower: TextView = viewDataBinding.votingPowerValue
    val votingPowerPercent: TextView = viewDataBinding.votingPowerPercentValue
    val address:TextView = viewDataBinding.address
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.validator_item
    }
}