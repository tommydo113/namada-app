package com.namada.app.ui.proposaldetail

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.namada.app.R
import com.namada.app.databinding.FragmentProposalDetailBinding
import com.namada.app.domain.Proposal


/**
 * A simple [Fragment] subclass.
 * Use the [ProposalDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProposalDetailFragment : Fragment() {
    private var screenWidth: Int = 1
    val args: ProposalDetailFragmentArgs by navArgs()
    val proposal: Proposal by lazy {
        args.proposal
    }
    private var _binding: FragmentProposalDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProposalDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.proposal = proposal
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getWindowWidth()
        val totalVote = proposal.yayVotes + proposal.nayVotes + proposal.abstainVotes
        if (totalVote > 0) {
            binding.yesTv.layoutParams.width =
                ((proposal.yayVotes * screenWidth) / totalVote).toInt()
            binding.noTv.layoutParams.width =
                ((proposal.nayVotes * screenWidth) / totalVote).toInt()
            binding.abstainTv.layoutParams.width =
                ((proposal.abstainVotes * screenWidth) / totalVote).toInt()
        }
        binding.proposalId.text = buildString {
            append("Proposal #")
            append(proposal.id)
        }
        binding.startEpoch.text = buildString {
            append("Start Epoch: ")
            append(proposal.startEpoch)
        }
        binding.endEpoch.text = buildString {
            append("End Epoch: ")
            append(proposal.endEpoch)
        }
        binding.graceEpoch.text = buildString {
            append("Grace Epoch: ")
            append(proposal.graceEpoch)
        }
        binding.yesNumber.text = buildString { append(proposal.yayVotes) }
        binding.noNumber.text = buildString { append(proposal.nayVotes) }
        binding.abstainNumber.text = buildString { append(proposal.abstainVotes) }
        binding.yesPercent.text =
            buildString {
                append(String.format("%.2f", proposal.yayVotes * 100 / totalVote)).append(
                    "%"
                )
            }
        binding.noPercent.text =
            buildString {
                append(String.format("%.2f", proposal.nayVotes * 100 / totalVote)).append(
                    "%"
                )
            }
        binding.details.text =  proposal.details
        binding.abstainPercent.text =
            buildString {
                append(
                    String.format(
                        "%.2f",
                        proposal.abstainVotes * 100 / totalVote
                    )
                ).append("%")
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

    companion object {


        @JvmStatic
        fun newInstance() =
            ProposalDetailFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}