package com.lenecoproekt.notes.viewmodel

import androidx.annotation.VisibleForTesting
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.NoteResult
import com.lenecoproekt.notes.model.Repository
import com.lenecoproekt.notes.ui.activity.NoteViewState
import com.lenecoproekt.notes.ui.base.BaseViewModel


class NoteViewModel(val repository: Repository) :
    BaseViewModel<NoteViewState.Data, NoteViewState>() {

    private val currentNote: Note?
        get() = viewStateLiveData.value?.data?.note

    fun saveChanges(note: Note) {
        viewStateLiveData.value = NoteViewState(NoteViewState.Data(note = note))
    }

    @VisibleForTesting
    public override fun onCleared() {
        currentNote?.let { repository.saveNote(it) }
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

