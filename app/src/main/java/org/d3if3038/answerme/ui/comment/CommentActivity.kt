package org.d3if3038.answerme.ui.comment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ViewUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import com.google.android.material.internal.ViewUtils.hideKeyboard
import org.d3if3038.answerme.R
import org.d3if3038.answerme.adapter.CommentAdapter
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.databinding.ActivityCommentBinding
import org.d3if3038.answerme.model.Comment
import org.d3if3038.answerme.model.FetchStatus
import org.d3if3038.answerme.model.Post

class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    private lateinit var commentAdapter: CommentAdapter

    private val postArgs: CommentActivityArgs by navArgs()

    private val viewModel: CommentViewModel by lazy {
        ViewModelProvider(this)[CommentViewModel::class.java]
    }
    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(applicationContext.dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        commentAdapter = CommentAdapter()

        setContentView(binding.root)

        with(binding) {
            commentRecycleView.adapter = commentAdapter
            topBar.topAppBar.setNavigationIcon(R.drawable.baseline_close_24)
            topBar.topAppBar.setNavigationOnClickListener { finish() }

            commentInputHint.setEndIconOnClickListener { postComment() }
        }



        viewModel.getPost().observe(this) { updatePostUI(it) }
        viewModel.getComments().observe(this) { commentAdapter.submitList(it) }
        viewModel.getCommentPostStatus().observe(this) {
            if (it != FetchStatus.SUCCESS) return@observe

            currentFocus?.let { view ->
                val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }

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