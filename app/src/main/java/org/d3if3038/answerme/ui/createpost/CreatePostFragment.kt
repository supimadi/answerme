package org.d3if3038.answerme.ui.createpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.databinding.FragmentCreatePostBinding
import org.d3if3038.answerme.model.Post

class CreatePostFragment : Fragment() {
    private lateinit var binding: FragmentCreatePostBinding

    private val viewModel: CreatePostViewModel by lazy {
        ViewModelProvider(this)[CreatePostViewModel::class.java]
    }
    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(requireActivity().dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false)

        binding.postButton.setOnClickListener { createPost() }

        viewModel.getMessage().observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_LONG
            ).show()
        }

        return binding.root
    }

    private fun createPost() {
        val ids: List<Int> = binding.chipGroup.checkedChipIds
        val selectedGenres = mutableListOf<String>()
        val username = settingDataStore.getString("username", "")

        ids.forEach {
            selectedGenres.add(
                binding.chipGroup.findViewById<Chip>(it).text.toString()
            )
        }

        val postTitle = binding.titleTextInput.text
        val question = binding.questionTextInput.text

        if (selectedGenres.isEmpty()) {
            Toast.makeText(requireContext(), "Genre(s) is Required!", Toast.LENGTH_LONG).show()
            return
        }

        if (postTitle.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Post Title is Required!", Toast.LENGTH_LONG).show()
            return
        }

        if (question.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Question is Required!", Toast.LENGTH_LONG).show()
            return
        }

        if (username.isEmpty()) {
            Toast.makeText(requireContext(), "Enter Your Username First in Setting!", Toast.LENGTH_LONG).show()
            return
        }

        val newPost = Post(
            username = username,
            title = postTitle.toString(),
            question = question.toString(),
            genres = selectedGenres
        )

        viewModel.pushPost(newPost)

    }
}