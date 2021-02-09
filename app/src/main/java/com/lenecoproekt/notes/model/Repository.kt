package com.lenecoproekt.notes.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lenecoproekt.notes.data.FireStoreProvider
import com.lenecoproekt.notes.data.RemoteDataProvider
import java.util.*


class Repository(private val remoteDataProvider: RemoteDataProvider) {

    suspend fun getNotes() = remoteDataProvider.subscribeToAllNotes()
    suspend fun saveNote(note: Note) = remoteDataProvider.saveNote(note)
    suspend fun getNoteById(id: String) = remoteDataProvider.getNoteById(id)
    suspend fun removeNote(id: String) = remoteDataProvider.removeNote(id)
    suspend fun getCurrentUser() = remoteDataProvider.getCurrentUser()
}