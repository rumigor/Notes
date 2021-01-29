package com.lenecoproekt.notes.ui.activity

import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.ui.base.BaseViewState

class NoteViewState(note: Note? = null, error: Throwable? = null) : BaseViewState<Note?>(note, error)