package com.lenecoproekt.notes.ui.activity

import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.ui.base.BaseViewState

class MainViewState(val notes: List<Note>? = null, error: Throwable? = null) :
    BaseViewState<List<Note>?>(notes, error)