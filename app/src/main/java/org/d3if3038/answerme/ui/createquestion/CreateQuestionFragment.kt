package org.d3if3038.answerme.ui.createquestion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import org.d3if3038.answerme.R
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.databinding.ActivityCreateQuestionBinding
import org.d3if3038.answerme.model.FetchStatus
import org.d3if3038.answerme.model.Post

class CreateQuestionFragment : Fragment() {
    private lateinit var binding: ActivityCreateQuestionBinding

    private var questionValid = true
    private var titleValid = true

    private val viewModel: CreateQuestionViewModel by lazy {
        ViewModelProvider(this)[CreateQuestionViewModel::class.java]
    }
    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(requireContext().dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateQuestionBinding.inflate(layoutInflater)

        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.postButton.setOnClickListener { createPost() }
        binding.titleTextInput.addTextChangedListener { text ->
            if (text.isNullOrEmpty()) return@addTextChangedListener
            checkTitleLength()
        }
        binding.questionTextInput.addTextChangedListener { text ->
            if (text.isNullOrEmpty()) return@addTextChangedListener
            checkQuestionLength()
        }

        with(binding.topBar) {
            topCollapsingToolbarLayout.title = getString(R.string.create_a_new_post)
            topAppBar.setNavigationIcon(R.drawable.baseline_close_24)
            topAppBar.setNavigationOnClickListener {

            }
        }

        viewModel.getMessage().observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_LONG
            ).show()
        }
        viewModel.getStatus().observe(viewLifecycleOwner) {
            if (it == FetchStatus.SUCCESS || it == FetchStatus.PENDING)
                findNavController().navigateUp()
        }

        binding.endView.visibility = View.GONE
    }

    private fun checkTitleLength() = with(binding.titleInputHint) {
        if (binding.titleTextInput.length() > 15) {
            this.isErrorEnabled = true
            this.error = context.getString(R.string.title_can_t_be_more_than_20_character)
            titleValid = false
        } else {
            this.isErrorEnabled = false
            titleValid = true
        }
    }

    private fun checkQuestionLength() = with(binding.questionInputHint) {
        if (binding.questionTextInput.length() > 150) {
            this.isErrorEnabled = true
            this.error = context.getString(R.string.question_can_t_be_more_than_150_character)
            questionValid = false
        } else {
            this.isErrorEnabled = false
            questionValid = true
        }

    }

    private fun createPost() {
        val ids: List<Int> = binding.chipGroup.checkedChipIds
        val selectedGenres = mutableListOf<String>()
        val username = settingDataStore.getString("username", "")
        val avatarUrl = settingDataStore.getString("dicebearLink", "")

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

        if (!titleValid || !questionValid) {
            Toast.makeText(requireContext(), "There is a problems, fix it please...", Toast.LENGTH_LONG).show()
            return
        }

        val newPost = Post(
            username = username,
            title = postTitle.toString(),
            question = question.toString(),
            genres = selectedGenres,
            avatar = avatarUrl
        )

        viewModel.pushPost(newPost)

    }
}