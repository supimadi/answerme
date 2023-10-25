package org.d3if3038.answerme.ui.feeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.d3if3038.answerme.databinding.FragmentFeedsBinding

class FeedsFragment : Fragment() {
    private lateinit var binding: FragmentFeedsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedsBinding.inflate(layoutInflater, container, false)

        binding.fabNewPost.setOnClickListener {
            findNavController().navigate(
                FeedsFragmentDirections.actionFeedsFragmentToCreatePostFragment()
            )
        }

        return binding.root
    }
}