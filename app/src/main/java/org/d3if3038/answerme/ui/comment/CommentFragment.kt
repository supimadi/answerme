package org.d3if3038.answerme.ui.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.d3if3038.answerme.R
import org.d3if3038.answerme.adapter.CommentAdapter
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.databinding.ActivityCommentBinding
import org.d3if3038.answerme.model.Comment
import org.d3if3038.answerme.model.FetchStatus
import org.d3if3038.answerme.model.Post

class CommentFragment : Fragment() {
    private lateinit var binding: ActivityCommentBinding
    private lateinit var commentAdapter: CommentAdapter

    private val postArgs: CommentFragmentArgs by navArgs()

    private val viewModel: CommentViewModel by lazy {
        ViewModelProvider(this)[CommentViewModel::class.java]
    }
    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(requireContext().dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityCommentBinding.inflate(layoutInflater)
        commentAdapter = CommentAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            commentRecycleView.adapter = commentAdapter
            topBar.topAppBar.setNavigationIcon(R.drawable.baseline_close_24)
            topBar.topAppBar.setNavigationOnClickListener { findNavController().navigateUp() }

            commentInputHint.setEndIconOnClickListener { postComment() }
        }

        viewModel.getPost().observe(viewLifecycleOwner) { updatePostUI(it) }
        viewModel.getComments().observe(viewLifecycleOwner) { commentAdapter.submitList(it) }
        viewModel.getCommentPostStatus().observe(viewLifecycleOwner) {
            if (it != FetchStatus.SUCCESS) return@observe

            // TODO: Need to implement hidden soft keyboard

            binding.commenttextinput.clearFocus()
            binding.commenttextinput.setText("")
        }
        viewModel.fetchQuestion(postArgs.documentId)
    }

    private fun postComment() {
        val commentText = binding.commenttextinput.text

        if (commentText.isNullOrEmpty()) return

        val comment = Comment(
            md5 = "asd",
            commentText = binding.commenttextinput.text.toString(),
            username = settingDataStore.getString("username", ""),
            avatarUrl = settingDataStore.getString("dicebearLink", "")
        )

        viewModel.postComment(comment, postArgs.documentId)

    }

    private fun updatePostUI(post: Post) = with(binding) {
        topBar.topCollapsingToolbarLayout.title = post.title
        questionText.text = post.question
        questionAuthor.text = post.username

        if (post.comments.isNullOrEmpty())
            emptyView.visibility = View.VISIBLE


        progressCircular.visibility = View.GONE
    }
}