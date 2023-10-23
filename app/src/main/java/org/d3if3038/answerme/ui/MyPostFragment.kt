package org.d3if3038.answerme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.d3if3038.answerme.databinding.FragmentMyPostBinding

class MyPostFragment : Fragment() {
    private lateinit var binding: FragmentMyPostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPostBinding.inflate(inflater, container, false)

        return binding.root
    }
}