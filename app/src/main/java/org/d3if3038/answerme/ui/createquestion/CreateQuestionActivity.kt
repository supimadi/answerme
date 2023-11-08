package org.d3if3038.answerme.ui.createquestion

import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import org.d3if3038.answerme.R
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.databinding.ActivityCreateQuestionBinding
import org.d3if3038.answerme.model.FetchStatus
import org.d3if3038.answerme.model.Post

class CreateQuestionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateQuestionBinding

    private var questionValid = true
    private var titleValid = true

    private val viewModel: CreateQuestionViewModel by lazy {
        ViewModelProvider(this)[CreateQuestionViewModel::class.java]
    }
    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(this.dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable Activity Transitions. Optionally enable Activity transitions in your
        // theme with <item name=”android:windowActivityTransitions”>true</item>.
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

        // Set the transition name, which matches Activity A’s start view transition name, on
        // the root view.
        findViewById<View>(android.R.id.content).transitionName = "shared_element_container"

        // Attach a callback used to receive the shared elements from Activity A to be
        // used by the container transform transition.
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())

        // Set this Activity’s enter and return transition to a MaterialContainerTransform
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 300L
        }

        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 250L
        }

        super.onCreate(savedInstanceState)
        binding = ActivityCreateQuestionBinding.inflate(layoutInflater)

        setContentView(binding.root)

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

        if (!titleValid || !questionValid) {
            Toast.makeText(this, "There is a problems, fix it please...", Toast.LENGTH_LONG).show()
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