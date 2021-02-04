
package com.lenecoproekt.notes.data

import androidx.lifecycle.LiveData
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.NoteResult
import com.lenecoproekt.notes.model.User

interface RemoteDataProvider {
    fun subscribeToAllNotes(): LiveData<NoteResult>
    fun getNoteById(id: String): LiveData<NoteResult>
    fun saveNote(note: Note) : LiveData<NoteResult>
    fun removeNote(id :String) : LiveData<NoteResult>
    fun getCurrentUser(): LiveData<User?>
}