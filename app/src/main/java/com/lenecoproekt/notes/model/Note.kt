package com.lenecoproekt.notes.model

import android.os.Parcelable
import java.util.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    val id: String = "",
    val title: String = "",
    val note: String = "",
    var color: Color = Color.WHITE,
    val lastChanged: Date = Date()
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

enum class Color (val position : Int) {
    WHITE (0),
    YELLOW (1),
    GREEN (2),
    BLUE (3),
    RED(4),
    VIOLET (5),
    PINK(6)
}