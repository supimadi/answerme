package org.d3if3038.answerme.ui.mypost

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.d3if3038.answerme.databinding.PostItemBinding
import org.d3if3038.answerme.model.Post

class MyPostAdapter : ListAdapter<Post, MyPostAdapter.ViewHolder>(DIFF_CALLBACK) {

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
        val profile = Firebase.firestore

        fun bind(item: Post) = with(binding) {
            this.author.text = item.username
            this.title.text = item.title
            this.questionText.text = item.question

            Glide.with(binding.root).load(item.avatar).into(this.profileImage)
        }

    }

}