package com.lenecoproekt.notes.viewmodel

import androidx.annotation.VisibleForTesting
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.Repository
import com.lenecoproekt.notes.ui.activity.Data
import com.lenecoproekt.notes.ui.base.BaseViewModel
import kotlinx.coroutines.launch


class NoteViewModel(val repository: Repository) :
    BaseViewModel<Data>() {

    private val currentNote: Note?
        get() = getViewState().tryReceive().getOrNull()?.note

    fun saveChanges(note: Note) {
        setData(Data(note = note))
    }



    fun loadNote(noteId: String) {
        launch {
            try {
                setData(Data(note = repository.getNoteById(noteId)))
            } catch (e : Throwable) {
                setError(e)
            }
        }
    }

    fun removeNote() {
        launch {
            try {
                currentNote?.let { repository.removeNote(it.id) }
                setData(Data(isRemoved = true))
            } catch (e : Throwable) {
                setError(e)
            }
        }
    }

    @VisibleForTesting
    public override fun onCleared() {
        launch {
            currentNote?.let {
                repository.saveNote(it) }
            super.onCleared()
        }
    }
}

