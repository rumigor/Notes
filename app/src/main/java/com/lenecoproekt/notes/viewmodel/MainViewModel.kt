package com.lenecoproekt.notes.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.lenecoproekt.notes.data.NoAuthException
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.NoteResult
import com.lenecoproekt.notes.model.Repository
import com.lenecoproekt.notes.model.User
import com.lenecoproekt.notes.ui.activity.MainViewState
import com.lenecoproekt.notes.ui.activity.SplashViewState
import com.lenecoproekt.notes.ui.base.BaseViewModel

class MainViewModel(val repository: Repository = Repository) :
    BaseViewModel<List<Note>?, MainViewState>() {

    private val repositoryNotes = repository.getNotes()
    private val notesObserver = object : Observer<NoteResult> {

        override fun onChanged(t: NoteResult?) {

            t?.let {
                when (it) {
                    is NoteResult.Success<*> -> {
                        viewStateLiveData.value = MainViewState(notes = it.data as? List<Note>)
                    }
                    is NoteResult.Error -> {
                        viewStateLiveData.value = MainViewState(error = it.error)
                    }
                }
            } ?: return
        }
    }

    init {
        viewStateLiveData.value = MainViewState()
        repositoryNotes.observeForever(notesObserver)
    }

    override fun onCleared() {
        repositoryNotes.removeObserver(notesObserver)
    }

    fun removeNote(id: String) {
        repository.removeNote(id)
    }

    fun deleteAllNotes(notes: List<Note>) {
        repository.deleteAllNotes(notes)
    }

    fun requestUser() : LiveData<User?> {
        return repository.getCurrentUser()
    }
}