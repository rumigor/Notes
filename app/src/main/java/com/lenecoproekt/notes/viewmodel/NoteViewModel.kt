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
    BaseViewModel<Note?, NoteViewState>() {

    private var pendingNote: Note? = null

    fun saveChanges(note: Note) {
        pendingNote = note
    }

    override fun onCleared() {
        pendingNote?.let {
            repository.saveNote(it)
        }
    }

    fun loadNote(noteId: String) {
        repository.getNoteById(noteId).observeForever { t ->
            t?.apply {
                when (this) {
                    is NoteResult.Success<*> ->
                        viewStateLiveData.value = NoteViewState(note = data as? Note)
                    is NoteResult.Error ->
                        viewStateLiveData.value = NoteViewState(error = error)
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
}

