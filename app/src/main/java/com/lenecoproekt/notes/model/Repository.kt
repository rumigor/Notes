package com.lenecoproekt.notes.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lenecoproekt.notes.data.FireStoreProvider
import com.lenecoproekt.notes.data.RemoteDataProvider
import java.util.*


class Repository(private val remoteDataProvider: RemoteDataProvider) {

    fun getNotes() = remoteDataProvider.subscribeToAllNotes()
    fun saveNote(note: Note) = remoteDataProvider.saveNote(note)
    fun getNoteById(id: String) = remoteDataProvider.getNoteById(id)
    fun removeNote(id: String) = remoteDataProvider.removeNote(id)
    fun getCurrentUser() = remoteDataProvider.getCurrentUser()
}