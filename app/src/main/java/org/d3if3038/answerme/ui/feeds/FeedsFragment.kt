package org.d3if3038.answerme.ui.feeds

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import org.d3if3038.answerme.MainActivity
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
                FeedsFragmentDirections.actionFeedPagesToCreateQuestionFragment()
            )
        }

        viewModel.getPosts().observe(viewLifecycleOwner) {
            binding.emptyView.visibility = if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
            binding.progressCircular.visibility = View.GONE
            binding.refreshButton.visibility = View.GONE

            questionAdapter.submitList(it)
        }
        viewModel.getnewPostNotif().observe(viewLifecycleOwner) {
            if (!it) return@observe

            binding.refreshButton.visibility = View.VISIBLE
        }

        binding.refreshButton.setOnClickListener {
            viewModel.getFeeds()
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                MainActivity.PERMISSION_REQUEST_CODE
            )

        }

    }
}