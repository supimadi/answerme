package org.d3if3038.answerme.ui.feeds

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
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

        viewModel.getFeeds()
        viewModel.getPosts().observe(viewLifecycleOwner) {
            binding.emptyView.visibility = if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
            binding.progressCircular.visibility = View.GONE

            questionAdapter.submitList(it)
        }

        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val genres = mutableListOf<String>()

            checkedIds.forEach {
                genres.add(
                    group.findViewById<Chip>(it).text.toString()
                )
            }

            viewModel.getFeeds(genres)
        }


        with(binding) {
            myPostRecycleView.adapter = questionAdapter
            topBar.topCollapsingToolbarLayout.title = getString(R.string.feeds)
        }


        return binding.root
    }
}