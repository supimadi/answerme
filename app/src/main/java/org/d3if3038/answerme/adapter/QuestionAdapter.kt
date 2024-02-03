package org.d3if3038.answerme.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.d3if3038.answerme.R
import org.d3if3038.answerme.databinding.PostItemBinding
import org.d3if3038.answerme.model.Post
import org.d3if3038.answerme.ui.feeds.FeedsFragmentDirections
import org.d3if3038.answerme.ui.myquestion.MyQuestionFragmentDirections
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class QuestionAdapter : ListAdapter<Post, QuestionAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Post>() {
                override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                    return oldItem.documentId == newItem.documentId
                }

                override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                    return oldItem == newItem
                }

            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ViewHolder (
        private val binding: PostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Post) = with(binding) {
            this.author.text = item.username
            this.title.text = item.title
            this.postText.text = item.question

            this.commentCounter.text = (if (item.comments.isNullOrEmpty()) {
                "0 Comments"
            } else {
                "${item.comments?.size} Comments"
            }).toString()
            this.commentCounter.visibility = View.VISIBLE

            val dateDiff =  TimeUnit.DAYS.convert(
                System.currentTimeMillis() - item.timeStamp,
                TimeUnit.MILLISECONDS
            )

            if (dateDiff > 7) {
                this.timeStampText.text = "${dateDiff / 7} w"
            } else {
                this.timeStampText.text = "$dateDiff d"
            }

            root.setOnClickListener {
                val docId = if (item.documentId.isNullOrEmpty()) {
                    Toast.makeText(
                        root.context,
                        root.context.getString(R.string.cannot_find_the_question),
                        Toast.LENGTH_LONG)
                        .show()
                    return@setOnClickListener
                } else {
                    item.documentId!!
                }

                try {
                    Navigation.findNavController(root).navigate(
                        MyQuestionFragmentDirections.actionMyQuestionPageToCommentFragment(docId)
                    )
                } catch (_: IllegalArgumentException) {
                    Navigation.findNavController(root).navigate(
                        FeedsFragmentDirections.actionFeedPagesToCommentFragment(docId)
                    )
                }

            }

            Glide.with(root).load(item.avatar).into(this.profileImage)
        }

    }

}