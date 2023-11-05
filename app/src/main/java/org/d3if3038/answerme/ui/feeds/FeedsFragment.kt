package org.d3if3038.answerme.ui.feeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.d3if3038.answerme.R
import org.d3if3038.answerme.adapter.QuestionAdapter
import org.d3if3038.answerme.databinding.FragmentFeedsBinding

class FeedsFragment : Fragment() {
    private lateinit var binding: FragmentFeedsBinding
    private lateinit var questionAdapter: QuestionAdapter

    private val viewModel: FeedsViewModel by lazy {
        ViewModelProvider(this)[FeedsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedsBinding.inflate(layoutInflater, container, false)
        questionAdapter = QuestionAdapter()

        binding.fabNewPost.setOnClickListener {
            findNavController().navigate(
                FeedsFragmentDirections.actionFeedPagesToCreateQuestionActivity()
            )
        }

        viewModel.fetchMyPost()
        viewModel.getPosts().observe(viewLifecycleOwner) {
            binding.emptyView.visibility = if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
            binding.progressCircular.visibility = View.GONE

            questionAdapter.submitList(it)
        }

        with(binding.myPostRecycleView) {
            adapter = questionAdapter

            setHasFixedSize(true)
        }

        binding.topBar.topCollapsingToolbarLayout.title = getString(R.string.feeds)

        return binding.root
    }
}