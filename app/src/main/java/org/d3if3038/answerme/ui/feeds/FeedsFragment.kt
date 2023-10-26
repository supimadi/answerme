package org.d3if3038.answerme.ui.feeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.GeneratedAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.d3if3038.answerme.adapter.PostAdapter
import org.d3if3038.answerme.databinding.FragmentFeedsBinding

class FeedsFragment : Fragment() {
    private lateinit var binding: FragmentFeedsBinding
    private lateinit var postAdapter: PostAdapter

    private val viewModel: FeedsViewModel by lazy {
        ViewModelProvider(this)[FeedsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedsBinding.inflate(layoutInflater, container, false)
        postAdapter = PostAdapter()

        binding.fabNewPost.setOnClickListener {
            findNavController().navigate(
                FeedsFragmentDirections.actionFeedsFragmentToCreatePostFragment()
            )
        }

        viewModel.fetchMyPost()
        viewModel.getPosts().observe(viewLifecycleOwner) {
            binding.emptyView.visibility = if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
            binding.progressCircular.visibility = View.GONE

            postAdapter.submitList(it)
        }

        with(binding.myPostRecycleView) {
            adapter = postAdapter

            setHasFixedSize(true)
        }

        return binding.root
    }
}