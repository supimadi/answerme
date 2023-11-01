package org.d3if3038.answerme.adapter

import android.view.LayoutInflater
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
        fun bind(item: Post) = with(binding) {
            this.author.text = item.username
            this.title.text = item.title
            this.postText.text = item.question

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
                        MyQuestionFragmentDirections.actionMyPostFragmentToCommentFragment(docId)
                    )
                } catch (_: IllegalArgumentException) {
                    Navigation.findNavController(root).navigate(
                        FeedsFragmentDirections.actionFeedsFragmentToCommentFragment(docId)
                    )
                }

            }

            Glide.with(root).load(item.avatar).into(this.profileImage)
        }

    }

}