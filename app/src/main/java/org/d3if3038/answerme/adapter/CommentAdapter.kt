package org.d3if3038.answerme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.d3if3038.answerme.databinding.PostItemBinding
import org.d3if3038.answerme.model.Comment

class CommentAdapter : ListAdapter<Comment, CommentAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Comment>() {
                override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                    return oldItem.md5 == newItem.md5
                }

                override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                    return oldItem == newItem
                }

            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostItemBinding.inflate(inflater, parent, false)

        return CommentAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ViewHolder (
        private val binding: PostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Comment) = with(binding) {
            this.author.text = item.username
            this.postText.text = item.commentText

            this.separator.visibility = View.GONE
            this.title.visibility = View.GONE

            Glide.with(binding.root).load(item.avatarUrl).into(this.profileImage)
        }

    }


}