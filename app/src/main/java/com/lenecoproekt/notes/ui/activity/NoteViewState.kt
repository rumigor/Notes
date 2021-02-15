package com.lenecoproekt.notes.ui.activity

import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.ui.base.BaseViewState

class NoteViewState(data : Data = Data(), error: Throwable? = null) :
    BaseViewState<NoteViewState.Data>(data, error) {

        data class Data(val isRemoved: Boolean = false, val note: Note? = null)
    }