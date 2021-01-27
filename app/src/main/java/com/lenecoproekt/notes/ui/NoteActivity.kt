package com.lenecoproekt.notes.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lenecoproekt.notes.R
import com.lenecoproekt.notes.databinding.ActivityNoteBinding
import com.lenecoproekt.notes.model.Color
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.ui.base.BaseActivity
import com.lenecoproekt.notes.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*


private const val SAVE_DELAY = 2000L

class NoteActivity : BaseActivity<Note?, NoteViewState>() {

    companion object {
        const val EXTRA_NOTE = "NoteActivity.extra.NOTE"
        fun getStartIntent(context: Context, noteId: String?): Intent {
            val intent = Intent(context, NoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE, noteId)
            return intent
        }
    }

    private var note: Note? = null
    override val ui: ActivityNoteBinding by lazy { ActivityNoteBinding.inflate(layoutInflater) }
    override val viewModel: NoteViewModel by lazy { ViewModelProvider(this).get(NoteViewModel::class.java) }
    private val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            // not used
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            triggerSaveNote()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // not used
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val noteId = intent.getStringExtra(EXTRA_NOTE)
        noteId?.let {
            viewModel.loadNote(it)
        }


        setSupportActionBar(ui.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (noteId == null ) supportActionBar?.title = getString(R.string.new_note_tilte)
        initView()
    }

    private fun initView() {
        ui.titleEdit.setText(note?.title ?: "")
        ui.bodyTextEdit.setText(note?.note ?: "")
        if (note != null) {
            val color = when (note?.color) {
                Color.WHITE -> {
                    ui.spinner.setSelection(0)
                    R.color.color_white

                }
                Color.VIOLET -> {
                    ui.spinner.setSelection(5)
                    R.color.color_violet
                }
                Color.YELLOW -> {
                    ui.spinner.setSelection(1)
                    R.color.color_yellow
                }
                Color.RED -> {
                    ui.spinner.setSelection(4)
                    R.color.color_red
                }
                Color.PINK -> {
                    ui.spinner.setSelection(6)
                    R.color.color_pink
                }
                Color.GREEN -> {
                    ui.spinner.setSelection(2)
                    R.color.color_green
                }
                Color.BLUE -> {
                    ui.spinner.setSelection(3)
                    R.color.color_blue
                }
                else -> {
                    ui.spinner.setSelection(0)
                    R.color.color_white
                }
            }
            ui.toolbar.setBackgroundColor(resources.getColor(color, theme))
        }
        ui.titleEdit.addTextChangedListener(textChangeListener)
        ui.bodyTextEdit.addTextChangedListener(textChangeListener)
        ui.spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                triggerSaveNote()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun triggerSaveNote() {
        if (ui.titleEdit.text == null ||
            (ui.titleEdit.text!!.length < 3 && ui.bodyTextEdit.text == null)) return

        Handler(Looper.getMainLooper()).postDelayed({
            note = note?.copy(
                title = ui.titleEdit.text.toString(),
                note = ui.bodyTextEdit.text.toString(),
                lastChanged = Date(),
                color = setColor(ui.spinner.selectedItemId)
            )
                ?: viewModel.createNewNote(
                    ui.titleEdit.text.toString(),
                    ui.bodyTextEdit.text.toString(),
                    setColor(ui.spinner.selectedItemId)
                )
            if (note != null) viewModel.saveChanges(note!!)
        }, SAVE_DELAY)
    }


    private fun setColor(selectedItemId: Long): Color {
        return when (selectedItemId) {
            0L -> Color.WHITE
            1L -> Color.YELLOW
            2L -> Color.GREEN
            3L -> Color.BLUE
            4L -> Color.RED
            5L -> Color.VIOLET
            6L -> Color.PINK
            else -> Color.WHITE
        }
    }

    override fun renderData(data: Note?) {
        this.note = data
        initView()
    }

}