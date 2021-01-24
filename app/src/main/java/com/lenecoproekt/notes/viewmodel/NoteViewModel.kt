package com.lenecoproekt.notes.viewmodel

import androidx.lifecycle.ViewModel
import com.lenecoproekt.notes.model.Color
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.Repository
import java.util.*

class NoteViewModel (private val repository: Repository = Repository) : ViewModel() {

    private var pendingNote: Note? = null

    fun saveChanges(note: Note) {
        pendingNote = note
    }

    override fun onCleared() {
        if (pendingNote != null) {
            repository.saveNote(pendingNote!!)
        }
    }

    fun createNewNote(title : String, body : String, color : Color):Note{
        val note = Note (UUID.randomUUID().toString(), title, body)
        note.color = color
        return note
    }
}