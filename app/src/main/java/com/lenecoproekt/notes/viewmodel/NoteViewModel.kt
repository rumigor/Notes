package com.lenecoproekt.notes.viewmodel

import android.util.Log
import androidx.lifecycle.Observer
import com.lenecoproekt.notes.model.Color
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.NoteResult
import com.lenecoproekt.notes.model.Repository
import com.lenecoproekt.notes.ui.activity.NoteViewState
import com.lenecoproekt.notes.ui.base.BaseViewModel
import java.util.*


class NoteViewModel(val repository: Repository = Repository) :
    BaseViewModel<NoteViewState.Data, NoteViewState>() {

    private val currentNote: Note?
        get() = viewStateLiveData.value?.data?.note

    private var pendingNote: Note? = null

    fun saveChanges(note: Note) {
        viewStateLiveData.value = NoteViewState(NoteViewState.Data(note = note))
    }

    override fun onCleared() {
        pendingNote?.let {
            repository.saveNote(it)
        }
    }

    fun loadNote(noteId: String) {
        repository.getNoteById(noteId).observeForever { t ->
            t?.let { noteResult ->
                viewStateLiveData.value = when (noteResult) {
                    is NoteResult.Success<*> ->
                        NoteViewState(NoteViewState.Data(note = noteResult.data as? Note))
                    is NoteResult.Error ->
                        NoteViewState(error = noteResult.error)
                }
            }
        }
    }

    fun createNewNote(title: String, body: String, color: Color): Note {
        val note = Note(UUID.randomUUID().toString(), title, body)
        note.color = color
        Log.d("NEW_NOTE", "Note created")
        return note
    }

    fun removeNote() {
        currentNote?.let {
            repository.removeNote(it.id).observeForever { result ->
                result?.let { noteResult ->
                    viewStateLiveData.value = when (noteResult) {
                        is NoteResult.Success<*> ->
                            NoteViewState(NoteViewState.Data(isRemoved = true))
                        is NoteResult.Error ->
                            NoteViewState(error = noteResult.error)
                    }
                }
            }
        }
    }
}

