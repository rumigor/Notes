package com.lenecoproekt.notes.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lenecoproekt.notes.model.Note

import com.lenecoproekt.notes.model.NoteResult
import com.lenecoproekt.notes.model.Repository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()
    private val mockRepository = mockk<Repository>()
    private val notesData = Channel<NoteResult>()
    private lateinit var viewModel: MainViewModel
    var testDispatcher = TestCoroutineDispatcher()


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { runBlocking { mockRepository.getNotes() } } returns notesData
        viewModel = MainViewModel(mockRepository)

    }

    @Test
    fun `should call getNotes once`() {
        verify(exactly = 1) { runBlocking { mockRepository.getNotes() } }
    }

    @Test
    fun `should return error`() {
        runBlocking {
            var result: Throwable? = null
            val testData = Throwable("error")
            viewModel.setError(testData)
            result = viewModel.getErrorChannel().receive()
            assertEquals(testData, result)
        }
    }

    @Test
    fun `should return data`() {
        runBlocking {
            var result: List<Note>? = null
            val testData = listOf(Note(id = "1"), Note(id = "2"))
            viewModel.setData(testData)
            result = viewModel.getViewState().receive()
            assertEquals(testData, result)
        }
    }


    @Test
    fun `should close channel`() {
        viewModel.onCleared()
        assertTrue(notesData.isClosedForSend && notesData.isClosedForReceive)
    }


    @After
    fun tearDown() {
        notesData.cancel()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}