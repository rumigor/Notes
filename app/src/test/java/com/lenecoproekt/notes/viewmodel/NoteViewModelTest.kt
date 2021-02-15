package com.lenecoproekt.notes.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.NoteResult
import com.lenecoproekt.notes.model.Repository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class NoteViewModelTest {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()
    private val mockRepository = mockk<Repository>()
    private val notesData = Channel<NoteResult>()
    private lateinit var viewModel: NoteViewModel
    var testDispatcher = TestCoroutineDispatcher()
    private val testNotes = listOf(
        Note("333", "title", "body"),
        Note("444", "title1", "body1"),
        Note("555", "title2", "body2")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { runBlocking { mockRepository.getNotes() } } returns notesData
        every { runBlocking { mockRepository.getNoteById(any()) } } returns testNotes[0]
        every { runBlocking { mockRepository.saveNote(any()) } } returns testNotes[0]
        every { runBlocking { mockRepository.removeNote(any()) } } returns null
        viewModel = NoteViewModel(mockRepository)
    }

    @Test
    fun `should save note on close`() {
        viewModel.saveChanges(testNotes[0])
        viewModel.onCleared()
        verify(exactly = 1) { runBlocking { mockRepository.saveNote(testNotes[0]) } }
    }

    @Test
    fun `should load note`() {
        runBlocking {
            viewModel.loadNote(testNotes[0].id)
            var result = viewModel.getViewState().receive().note
            assertEquals(testNotes[0], result)
        }
    }


    @Test
    fun `should return true`() {
        runBlocking {
            viewModel.removeNote()
            var result = viewModel.getViewState().receive().isRemoved
            assertTrue(result)
        }
    }

    @Test
    fun `should save note`() {
        runBlocking {
            viewModel.saveChanges(testNotes[0])
            var result = viewModel.getViewState().receive().note
            assertEquals(testNotes[0], result)
        }
    }


    @After
    fun tearDown() {
        notesData.cancel()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}