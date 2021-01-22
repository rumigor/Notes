package com.lenecoproekt.notes.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.lenecoproekt.notes.R
import com.lenecoproekt.notes.databinding.ActivityNoteBinding
import com.lenecoproekt.notes.model.Color
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*

private const val SAVE_DELAY = 2000L

class NoteActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOTE = "NoteActivity.extra.NOTE"
        fun getStartIntent(context: Context, note: Note?): Intent {
            val intent = Intent(context, NoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE, note)
            return intent
        }
    }

    private var note: Note? = null
    private lateinit var ui: ActivityNoteBinding
    private lateinit var viewModel : NoteViewModel
    private val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            triggerSaveNote()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // not used
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // not used
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(ui.root)

        viewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        note = intent.getParcelableExtra(EXTRA_NOTE)
        initView()
        setSupportActionBar(ui.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (note != null) {
            SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(note!!.lastChanged)
        } else {
            getString(R.string.new_note_tilte)
        }
    }

    private fun initView() {
        ui.titleEdit.setText(note?.title ?: "")
        ui.bodyTextEdit.setText(note?.note ?: "")
        if (note != null) {
            val color = when (note?.color) {
                Color.WHITE -> R.color.color_white
                Color.VIOLET -> R.color.color_violet
                Color.YELLOW -> R.color.color_yellow
                Color.RED -> R.color.color_red
                Color.PINK -> R.color.color_pink
                Color.GREEN -> R.color.color_green
                Color.BLUE -> R.color.color_blue
                else -> R.color.color_white
            }
            ui.toolbar.setBackgroundColor(resources.getColor(color, theme))
        }
        ui.titleEdit.addTextChangedListener(textChangeListener)
        ui.bodyTextEdit.addTextChangedListener(textChangeListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun triggerSaveNote() {
        if (ui.titleEdit.text == null || ui.titleEdit.text!!.length < 3) return

        Handler(Looper.getMainLooper()).postDelayed({
            note = note?.copy(title = ui.titleEdit.text.toString(),
                note = ui.bodyTextEdit.text.toString(),
                lastChanged = Date())
                ?: createNewNote()
            if (note != null) viewModel.saveChanges(note!!)
        }, SAVE_DELAY)
    }


    private fun createNewNote(): Note = Note(
        UUID.randomUUID().toString(),
        ui.titleEdit.text.toString(),
        ui.bodyTextEdit.text.toString())
}