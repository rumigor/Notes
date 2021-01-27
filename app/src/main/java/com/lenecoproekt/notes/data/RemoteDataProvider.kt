package com.lenecoproekt.notes.data

import androidx.lifecycle.LiveData
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.NoteResult

interface RemoteDataProvider {
    fun subscribeToAllNotes(): LiveData<NoteResult>
    fun getNoteById(id: String): LiveData<NoteResult>
    fun saveNote(note: Note) : LiveData<NoteResult>
    fun removeNote(id :String) : LiveData<NoteResult>
    fun deleteAllNotes(notes: List<Note>) : LiveData<NoteResult>

}