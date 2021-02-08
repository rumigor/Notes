package com.lenecoproekt.notes.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.NoteResult
import com.lenecoproekt.notes.model.Repository
import com.lenecoproekt.notes.ui.activity.MainViewState
import com.lenecoproekt.notes.ui.activity.NoteViewState
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class NoteViewModelTest {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()
    private val mockRepository = mockk<Repository>()
    private val notesLiveData = MutableLiveData<NoteResult>()
    private val viewStateLiveData = MutableLiveData<NoteViewState>()
    private lateinit var viewModel: NoteViewModel
    private val testNotes = listOf(
        Note("333", "title", "body"),
        Note("444", "title1", "body1"),
        Note("555", "title2", "body2")
    )
    @Before
    fun setUp() {

        every { mockRepository.getNoteById(any()) } returns notesLiveData
        every { mockRepository.removeNote(any()) } returns mockk()
        viewModel = NoteViewModel(mockRepository)

    }

    @Test
    fun `should load note`() {
        var result: NoteViewState.Data? = null
        val testData = NoteViewState.Data(false, testNotes[1])
        viewModel.getViewState().observeForever { result = it.data }
        notesLiveData.value = NoteResult.Success(testData)
        viewModel.loadNote(testNotes[1].id)
        assertEquals(testData, result)
    }

    @Test
    fun `should remove observer`() {
        viewModel.onCleared()
        assertFalse(notesLiveData.hasObservers())
    }

    @Test
    fun `should remove note`() {
//        val testData = NoteViewState.Data(true)
//        viewStateLiveData.postValue(NoteViewState(NoteViewState.Data(true)
//
//        notesLiveData.value = NoteResult.Success(testData)
//        viewModel.removeNote()
//        assertEquals(testData, viewModel.getViewState().value?.data)
    }


    @After
    fun tearDown() {
    }
}