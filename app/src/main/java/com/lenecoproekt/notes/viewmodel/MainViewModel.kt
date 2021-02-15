package com.lenecoproekt.notes.viewmodel


import androidx.annotation.VisibleForTesting
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.NoteResult
import com.lenecoproekt.notes.model.Repository
import com.lenecoproekt.notes.model.User
import com.lenecoproekt.notes.ui.base.BaseViewModel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel(val repository: Repository) :
    BaseViewModel<List<Note>?>() {

    private val notesChannel by lazy { runBlocking { repository.getNotes() } }
    private var notes: List<Note>? = null

    init {
        launch {
            notesChannel.consumeEach { result ->
                when (result) {
                    is NoteResult.Success<*> -> {
                        setData(result.data as? List<Note>)
                        notes = result.data as? List<Note>
                    }
                    is NoteResult.Error -> setError(result.error)
                }
            }
        }
    }


    @VisibleForTesting
    public override fun onCleared() {
        notesChannel.cancel()
        super.onCleared()
    }

    fun removeNote(id: String) {
        launch {
            repository.removeNote(id)
        }
    }


    fun deleteAllNotes() {
        launch {
            notes?.let {
                for (note in it) {
                    repository.removeNote(note.id)
                }
            }
        }
    }

    fun requestUser() : User? = runBlocking { repository.getCurrentUser() }
}