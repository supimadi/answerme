package org.d3if3038.answerme.ui.createquestion

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import org.d3if3038.answerme.R
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.databinding.ActivityCreateQuestionBinding
import org.d3if3038.answerme.model.FetchStatus
import org.d3if3038.answerme.model.Post

class CreateQuestionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateQuestionBinding

    private val viewModel: CreateQuestionViewModel by lazy {
        ViewModelProvider(this)[CreateQuestionViewModel::class.java]
    }
    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(this.dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateQuestionBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.postButton.setOnClickListener { createPost() }

        with(binding.topBar) {
            topCollapsingToolbarLayout.title = getString(R.string.create_a_new_post)
            topAppBar.setNavigationIcon(R.drawable.baseline_close_24)
            topAppBar.setNavigationOnClickListener {
                finish()
            }
        }


        viewModel.getMessage().observe(this) {
            Toast.makeText(
                applicationContext,
                it,
                Toast.LENGTH_LONG
            ).show()
        }
        viewModel.getStatus().observe(this) {
            if (it == FetchStatus.SUCCESS)
                finish()
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
            Toast.makeText(this, "Genre(s) is Required!", Toast.LENGTH_LONG).show()
            return
        }

        if (postTitle.isNullOrEmpty()) {
            Toast.makeText(this, "Post Title is Required!", Toast.LENGTH_LONG).show()
            return
        }

        if (question.isNullOrEmpty()) {
            Toast.makeText(this, "Question is Required!", Toast.LENGTH_LONG).show()
            return
        }

        if (username.isEmpty()) {
            Toast.makeText(this, "Enter Your Username First in Setting!", Toast.LENGTH_LONG).show()
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