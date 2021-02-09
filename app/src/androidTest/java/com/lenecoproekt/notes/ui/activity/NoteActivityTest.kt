package com.lenecoproekt.notes.ui.activity

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.lenecoproekt.notes.R
import com.lenecoproekt.notes.model.Color
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.ui.getColorInt
import com.lenecoproekt.notes.viewmodel.NoteViewModel
import io.mockk.*
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class NoteActivityTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(NoteActivity::class.java, true, false)

    private val viewModel: NoteViewModel = spyk(NoteViewModel(mockk()))
    private val testNote = Note("333", "title", "body")

    @Before
    fun setUp() {
        startKoin {
            modules()
        }
        loadKoinModules(
            listOf(
                module {
                    viewModel { viewModel }
                })
        )
//        every { viewModel.getViewState() } returns viewStateLiveData
        every { viewModel.loadNote(any()) } just runs
        every { viewModel.saveChanges(any()) } just runs
        every { viewModel.removeNote() } just runs

        activityTestRule.launchActivity(Intent().apply {
            putExtra("NoteActivity.extra.NOTE", testNote.id)
        })

    }


    @Test
    fun should_show_color_picker() {
        onView(withId(R.id.palette)).perform(click())

        onView(withId(R.id.colorPicker)).check(matches(isCompletelyDisplayed()))
    }


    @Test
    fun should_hide_color_picker() {
        onView(withId(R.id.palette)).perform(click()).perform(click())

        onView(withId(R.id.colorPicker)).check(matches(not(isDisplayed())))
    }


    @Test
    fun should_set_toolbar_color() {
        onView(withId(R.id.palette)).perform(click())
        onView(withTagValue(`is`(Color.BLUE))).perform(click())

        val colorInt = Color.BLUE.getColorInt(activityTestRule.activity)

        onView(withId(R.id.toolbar)).check { view, _ ->
            assertTrue(
                "toolbar background color does not match",
                (view.background as? ColorDrawable)?.color == colorInt
            )
        }
    }

    @Test
    fun should_call_viewModel_loadNote() {
        verify(exactly = 1) { viewModel.loadNote(testNote.id) }
    }


    @Test
    fun should_show_note() {
        activityTestRule.launchActivity(null)
//        viewStateLiveData.postValue(NoteViewState(NoteViewState.Data(note = testNote)))

        onView(withId(R.id.titleEdit)).check(matches(withText(testNote.title)))
        onView(withId(R.id.bodyTextEdit)).check(matches(withText(testNote.note)))
    }


    @Test
    fun should_call_saveNote() {
        onView(withId(R.id.titleEdit)).perform(typeText(testNote.title))
        verify(timeout = 1000) { viewModel.saveChanges(any()) }
    }

    @Test
    fun should_call_removeNote() {
        openActionBarOverflowOrOptionsMenu(activityTestRule.activity)
        onView(withText(R.string.delete_dialog_message)).perform(click())
        onView(withText(R.string.ok_bth_title)).perform(click())
        verify (exactly = 1) { viewModel.removeNote() }
    }


    @After
    fun tearDown() {
        stopKoin()
    }
}