package com.lenecoproekt.notes.ui.activity


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import com.lenecoproekt.notes.R
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.NoteResult
import com.lenecoproekt.notes.ui.base.BaseActivity

import com.lenecoproekt.notes.viewmodel.MainViewModel
import com.lenecoproekt.notes.viewmodel.NoteViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.Dispatchers
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before


import org.junit.Rule
import org.junit.Test
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module


class MainActivityTest {

    @get:Rule
    val activityTestRule = IntentsTestRule(MainActivity::class.java, true, false)


    private val EXTRA_NOTE = "NoteActivity.extra.NOTE"
    var testDispatcher =  Dispatchers.Default
    private val viewModel: MainViewModel = spyk(MainViewModel(mockk()))

    private val notesData = Channel<List<Note>?>()


    private val testNotes = listOf(
        Note("333", "title", "body"),
        Note("444", "title1", "body1"),
        Note("555", "title2", "body2")
    )


    @Before
    fun setUp() {
        startKoin {
            modules()
        }
        loadKoinModules(
            listOf(
                module {
                    viewModel { viewModel }
                    viewModel { mockk<NoteViewModel>(relaxed = true) }
                })
        )

        runBlocking {
            viewModel.setData(testNotes)
            every { viewModel.requestUser()?.name } returns ""
        }

       runBlocking {
           notesData.send(testNotes)
           every { viewModel.getViewState() } returns notesData
       }

        activityTestRule.launchActivity(null)

    }

    @Test
    fun check_data_is_displayed() {
        onView(withId(R.id.mainRecycler))
            .perform(scrollToPosition<MainAdapter.NoteViewHolder>(1))
        onView(withText(testNotes[1].note)).check(matches(isDisplayed()))
    }

    @Test
    fun check_detail_activity_intent_sent() {
        onView(withId(R.id.mainRecycler))
            .perform(actionOnItemAtPosition<MainAdapter.NoteViewHolder>(1, click()))

        intended(
            allOf(
                hasComponent(NoteActivity::class.java.name),
                hasExtra(EXTRA_NOTE, testNotes[1].id)
            )
        )
    }


    @After
    fun tearDown() {
        stopKoin()
        testDispatcher.cancel()
    }
}