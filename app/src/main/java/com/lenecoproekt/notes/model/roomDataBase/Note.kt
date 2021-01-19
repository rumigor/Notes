package com.lenecoproekt.notes.model.roomDataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["title", "note", "color"])])
class Note {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo
    var title: String? = null

    @ColumnInfo
    var note: String? = null

    @ColumnInfo
    var color = 0
}