package org.d3if3038.answerme

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.model.Comment
import org.d3if3038.answerme.model.Post
import org.d3if3038.answerme.model.Profile
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MainActivityTest {

    companion object {
        private const val USERNAME = "TST"
        private const val DICEBEAR_URL = "https://api.dicebear.com/7.x/adventurer/png?backgroundColor=b6e3f4,c0aede,d1d4f9&seed=LKM"

        private val PROFIILE_DUMMY = Profile(
            USERNAME,
            DICEBEAR_URL
        )

        private val QUESTION_DUMMY = Post(
            "testingFromHome",
            USERNAME,
            DICEBEAR_URL,
            "#I'mjusttestin!",
            mutableListOf("Testing"),
            "I'mJustTesting!",
            null,
            false,
        )

        private val COMMENT_DUMMY = Comment(
            "asd",
            "Tes Comment",
            USERNAME,
            DICEBEAR_URL
        )
    }

    private var activityScenario: ActivityScenario<MainActivity>? = null
    private val firebaseDb = Firebase.firestore

    @Before
    fun setActivityScenario() {
        runBlocking {
            InstrumentationRegistry.getInstrumentation()
                .targetContext
                .dataStore.edit {
                    it[booleanPreferencesKey("is_boarded")] = true
                }
        }

        firebaseDb.collection("profile").document(USERNAME)
            .delete()

        activityScenario = ActivityScenario.launch(
            MainActivity::class.java
        )
    }

    @After
    fun closeActivityScenario() {
        runBlocking {
            InstrumentationRegistry.getInstrumentation()
                .targetContext
                .dataStore.edit {
                    it.clear()

                    it[booleanPreferencesKey("is_boarded")] = true
                    it[stringPreferencesKey("username")] = USERNAME
                    it[stringPreferencesKey("dicebearLink")] = DICEBEAR_URL
                }
        }
        activityScenario?.close()
    }

    @Test
    fun aSetupUsernameUser() {
        runBlocking {
            InstrumentationRegistry.getInstrumentation()
                .targetContext
                .dataStore.edit {
                    it.clear()

                    it[booleanPreferencesKey("is_boarded")] = true
                }

            launch {
                delay(1000L)
            }
        }

        onView(withId(R.id.settingPage)).perform(click())
        onView(withId(R.id.usernameInputText))
            .perform(typeText(PROFIILE_DUMMY.username))
        onView(withId(R.id.saveProfileBtn)).perform(
            click()
        )
    }

    @Test
    fun bFragmentCreateQuestion() {
        runBlocking {
            InstrumentationRegistry.getInstrumentation()
                .targetContext
                .dataStore.edit {
                    it[booleanPreferencesKey("is_boarded")] = true
                    it[stringPreferencesKey("username")] = USERNAME
                    it[stringPreferencesKey("dicebearLink")] = DICEBEAR_URL
                }
        }

        onView(withId(R.id.fabNewPost)).perform(click())

        onView(withId(R.id.chipFilm)).perform(click())
        onView(withId(R.id.titleTextInput)).perform(typeText(
            QUESTION_DUMMY.title
        ))
        onView(withId(R.id.questionTextInput)).perform(typeText(
            QUESTION_DUMMY.question
        ))
        onView(withId(R.id.postButton)).perform(click())
    }

    @Test
    fun cTestFeedFragment() {
        onView(withId(R.id.feedPages)).perform(click())
        onView(withId(R.id.feedRecycleView)).atItem(0, click())
        onView(withId(R.id.commentFragmentLayout)).check(matches(isDisplayed()))
    }

    @Test
    fun dFragmentSettingShowed() {
        onView(withId(R.id.settingPage)).perform(click())
        onView(withId(R.id.settingPageLayout)).perform(click())
    }

    @Test
    fun eFragmentMyQuestions() {
        onView(withId(R.id.myQuestionPage)).perform(click())
        onView(withId(R.id.myQuestionPageLayout)).check(matches(isDisplayed()))
    }

    @Test
    fun fInsertComment() {
        // Navigate to the page
        onView(withId(R.id.feedPages)).perform(click())
        onView(withId(R.id.feedRecycleView)).atItem(0, click())

        // insert comment data
        onView(withId(R.id.commenttextinput)).perform(
            typeText(COMMENT_DUMMY.commentText)
        )
    }

    @Test
    fun gCheckMyPostFragment() {
        onView(withId(R.id.myQuestionPage)).perform(click())
        onView(withId(R.id.myPostRecycleView)).atItem(0, click())
        onView(withId(R.id.commentFragmentLayout)).check(matches(isDisplayed()))

        deleteFirebaseData()
    }

    private fun ViewInteraction.atItem(pos: Int, action: ViewAction) {
        perform(
            RecyclerViewActions
            .actionOnItemAtPosition<RecyclerView.ViewHolder>(pos, action)
        )
    }

    private fun deleteFirebaseData() {
        firebaseDb.collection("posts")
            .whereEqualTo("title", QUESTION_DUMMY.title)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach {
                    it.reference.delete()
                }
            }
    }

}