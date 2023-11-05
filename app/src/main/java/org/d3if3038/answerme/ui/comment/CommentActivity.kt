package org.d3if3038.answerme.ui.comment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import org.d3if3038.answerme.databinding.ActivityCommentBinding

class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding

    private val viewModel: CommentViewModel by lazy {
        ViewModelProvider(this)[CommentViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}