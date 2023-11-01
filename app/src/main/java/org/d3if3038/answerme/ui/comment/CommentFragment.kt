package org.d3if3038.answerme.ui.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.d3if3038.answerme.databinding.FragmentCommentBinding
import org.d3if3038.answerme.model.Comment

class CommentFragment : Fragment() {
    private lateinit var binding: FragmentCommentBinding

    private val viewModel: CommentViewModel by lazy {
        ViewModelProvider(this)[CommentViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentBinding.inflate(inflater, container, false)

        return binding.root

    }
}