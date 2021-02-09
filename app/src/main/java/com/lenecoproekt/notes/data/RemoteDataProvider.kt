
package com.lenecoproekt.notes.data


import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.NoteResult
import com.lenecoproekt.notes.model.User
import kotlinx.coroutines.channels.ReceiveChannel

interface RemoteDataProvider {
    suspend fun subscribeToAllNotes(): ReceiveChannel<NoteResult>
    suspend fun getNoteById(id: String): Note
    suspend fun saveNote(note: Note) : Note
    suspend fun removeNote(id :String) : Note?
    suspend fun getCurrentUser(): User?
}