package com.lenecoproekt.notes.model.roomDataBase

import androidx.room.*

@Dao
interface NotesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: Note?)

    @Update
    fun updateNote(note: Note?)

    @Delete
    fun deleteNote(note: Note?)

    @Query("DELETE FROM Note WHERE id = :id")
    fun deleteNoteById(id: Long)

    @get:Query("SELECT * FROM Note")
    val allNotes: List<Note?>?

    @Query("SELECT * FROM Note WHERE id = :id")
    fun getNoteById(id: Long): Note?

    @Query("SELECT * FROM Note WHERE title = :title")
    fun getNoteByTitle(title: String?): List<Note?>?

    @get:Query("SELECT COUNT() FROM Note")
    val countNoteList: Long


    @Query("SELECT COUNT() FROM Note WHERE title = :title")
    fun getFilteredCountNote(title: String?): Long
}