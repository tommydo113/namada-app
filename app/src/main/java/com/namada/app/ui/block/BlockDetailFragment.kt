package com.namada.app.ui.block

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.namada.app.R

/**
 * A simple [Fragment] subclass.
 * Use the [BlockDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlockDetailFragment : Fragment() {
    val args: BlockDetailFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_block_detail, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val block = args.block
        println("block $block")

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