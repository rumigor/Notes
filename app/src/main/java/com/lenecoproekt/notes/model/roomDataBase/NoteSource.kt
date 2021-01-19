package com.lenecoproekt.notes.model.roomDataBase

class NoteSource(private val notesDao: NotesDao) {
    private var noteList: List<Note?>? = null
    fun getNoteList(): List<Note?>? {
        if (noteList == null) {
            LoadNoteList()
        }
        return noteList
    }

    fun LoadNoteList() {
        noteList = notesDao.allNotes
    }

    fun addNote(note: Note?) {
        notesDao.insertNote(note)
        LoadNoteList()
    }

    fun updateNote(note: Note?) {
        notesDao.updateNote(note)
        LoadNoteList()
    }

    fun removeNote(id: Long) {
        notesDao.deleteNoteById(id)
        LoadNoteList()
    }

    fun filterStoryByTitle(title: String?): List<Note?>? {
        noteList = notesDao.getNoteByTitle(title)
        return noteList
    }

    fun getFilteredNoteCount(title: String?): Long {
        return notesDao.getFilteredCountNote(title)
    }
}