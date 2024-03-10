package com.namada.app.ui.home

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
import com.namada.app.databinding.BlockItemBinding
import com.namada.app.databinding.FragmentHomeBinding
import com.namada.app.domain.Block

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var swipeContainer: SwipeRefreshLayout
    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onActivityCreated, which we
     * do in this Fragment.
     */
    private val viewModel: HomeViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this)[HomeViewModel::class.java]
    }
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var viewModelAdapter: BlockAdapter? = null

    /**
     * Called immediately after onCreateView() has returned, and fragment's
     * view hierarchy has been created. It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val root: View = binding.root

        viewModelAdapter = BlockAdapter(BlockClick {

            // context is not around, we can safely discard this click since the Fragment is no
            // longer on the screen
            println("click")

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
        viewModel.blocks.observe(viewLifecycleOwner) { blocks ->
            blocks?.apply {
                viewModelAdapter?.blocks = blocks
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
class BlockClick(val block: (Block) -> Unit) {
    /**
     * Called when a Block is clicked
     *
     * @param block the Block that was clicked
     */
    fun onClick(block: Block) = block(block)
}

/**
 * RecyclerView Adapter for setting up data binding on the items in the list.
 */
class BlockAdapter(val callback: BlockClick) : RecyclerView.Adapter<BlockViewHolder>() {

    /**
     * The Blocks that our Adapter will show
     */
    var blocks: List<Block> = emptyList()
        set(value) {
            field = value
            // For an extra challenge, update this to use the paging library.

            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val withDataBinding: BlockItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            BlockViewHolder.LAYOUT,
            parent,
            false)
        return BlockViewHolder(withDataBinding)
    }

    override fun getItemCount() = blocks.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.block = blocks[position]
            it.blockCallback = callback
        }
        val item = blocks[position]
        holder.blockHeight.text = ""+ item.height
        holder.txCount.text = "Tx: "+ item.txCount
        holder.proposer.text = "Proposer: "+ item.proposerAddress
    }

    fun clear() {
        blocks = emptyList()
        notifyDataSetChanged()
    }

}
/**
 * ViewHolder for Block items. All work is done by data binding.
 */
class BlockViewHolder(val viewDataBinding: BlockItemBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    val blockHeight: TextView = viewDataBinding.blockHeight
    val txCount: TextView = viewDataBinding.txCount
    val proposer: TextView = viewDataBinding.proposer
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.block_item
    }
}