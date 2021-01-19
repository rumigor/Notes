package com.lenecoproekt.notes.model.roomDataBase

import android.app.Application
import androidx.room.Room

class App : Application() {
    private var db: NotesDatabase? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        db = Room.databaseBuilder(
            applicationContext,
            NotesDatabase::class.java,
            "note_database"
        )
            .allowMainThreadQueries()
            .build()
    }

    companion object {
        var instance: App? = null
            private set
    }
}