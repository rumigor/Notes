package com.lenecoproekt.notes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lenecoproekt.notes.model.Repository
import com.lenecoproekt.notes.model.roomDataBase.App
import com.lenecoproekt.notes.model.roomDataBase.NotesDao
import com.lenecoproekt.notes.ui.MainViewState

class MainViewModel : ViewModel() {
    private val viewStateLiveData: MutableLiveData<MainViewState> = MutableLiveData()

    init{
       Repository.getNotes().observeForever {
            viewStateLiveData.value =
                viewStateLiveData.value?.copy(notes = it) ?: MainViewState(it)
        }
    }

    fun viewState(): LiveData<MainViewState> = viewStateLiveData
}