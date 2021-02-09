package com.lenecoproekt.notes.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.lenecoproekt.notes.R
import com.lenecoproekt.notes.databinding.ActivityNoteBinding
import com.lenecoproekt.notes.model.Color
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.ui.base.BaseActivity
import com.lenecoproekt.notes.ui.format
import com.lenecoproekt.notes.ui.getColorInt
import com.lenecoproekt.notes.ui.getColorRes
import com.lenecoproekt.notes.viewmodel.MainViewModel
import com.lenecoproekt.notes.viewmodel.NoteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


private const val SAVE_DELAY = 1000L

class NoteActivity : BaseActivity<Data>() {

    companion object {
        const val EXTRA_NOTE = "NoteActivity.extra.NOTE"
        fun getStartIntent(context: Context, noteId: String?): Intent {
            val intent = Intent(context, NoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE, noteId)
            return intent
        }
    }

    private var changeTextColor: Boolean = false
    private var note: Note? = null
    private var color: Color = Color.YELLOW
    private var textColor: Color = Color.BLACK
    override val ui: ActivityNoteBinding by lazy { ActivityNoteBinding.inflate(layoutInflater) }
    override val viewModel: NoteViewModel by viewModel()
    private val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            triggerSaveNote()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(ui.toolbar)
        val noteId = intent.getStringExtra(EXTRA_NOTE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        noteId?.let {
            viewModel.loadNote(it)
        } ?: run {
            supportActionBar?.title = "New note"
        }
        ui.colorPicker.onColorClickListener = {
            if (changeTextColor) {
                textColor = it
                ui.titleEdit.setTextColor(it.getColorInt(this@NoteActivity))
                ui.bodyTextEdit.setTextColor(it.getColorInt(this@NoteActivity))
                triggerSaveNote()
            } else {
                color = it
                ui.titleEdit.setBackgroundColor(it.getColorInt(this@NoteActivity))
                ui.bodyTextEdit.setBackgroundColor(it.getColorInt(this@NoteActivity))
                ui.noteContainer.setBackgroundColor(it.getColorInt(this@NoteActivity))
                setToolbarColor(it)
                triggerSaveNote()
            }
        }
        setEditListener()
    }

    private fun initView() {

        note?.run {
            removeEditListener()
            if (title != ui.titleEdit.text.toString()) {
                ui.titleEdit.setText(title)
                ui.titleEdit.setTextColor(textColor.getColorInt(this@NoteActivity))
                ui.titleEdit.setBackgroundColor(color.getColorInt(this@NoteActivity))
            }
            if (note != ui.bodyTextEdit.text.toString()) {
                ui.bodyTextEdit.setText(note)
                ui.bodyTextEdit.setTextColor(textColor.getColorInt(this@NoteActivity))
                ui.bodyTextEdit.setBackgroundColor(color.getColorInt(this@NoteActivity))
            }
            ui.noteContainer.setBackgroundColor(color.getColorInt(this@NoteActivity))
            setEditListener()
            supportActionBar?.title = lastChanged.format()
            setToolbarColor(color)
        }
    }

    private fun setToolbarColor(color: Color) {
        ui.toolbar.setBackgroundColor(color.getColorInt(this@NoteActivity))
        when (color) {
            Color.WHITE, Color.YELLOW -> ui.toolbar.setTitleTextColor(
                resources.getColor(
                    R.color.black,
                    theme
                )
            )
            else -> ui.toolbar.setTitleTextColor(
                resources.getColor(
                    R.color.white,
                    theme
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
        menuInflater.inflate(R.menu.menu_note, menu).let { true }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> super.onBackPressed().let { true }
        R.id.palette -> togglePalette(false).let { true }
        R.id.textPalette -> togglePalette(true).let { true }
        R.id.delete -> removeNote().let { true }
        else -> super.onOptionsItemSelected(item)
    }

    private fun togglePalette(textColor: Boolean) {
        if (ui.colorPicker.isOpen) {
            ui.colorPicker.close()
        } else {
            changeTextColor = textColor
            ui.colorPicker.open()
        }
    }

    private fun removeNote() {
        AlertDialog.Builder(this)
            .setMessage(R.string.delete_dialog_message)
            .setNegativeButton(R.string.logout_dialog_cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.ok_bth_title) { _, _ -> viewModel.removeNote() }
            .show()
    }

    private fun triggerSaveNote() {
        if (ui.titleEdit.text == null || ui.titleEdit.text!!.length < 3) return

        launch {
            delay(SAVE_DELAY)

            note = note?.copy(
                title = ui.titleEdit.text.toString(),
                note = ui.bodyTextEdit.text.toString(),
                lastChanged = Date(),
                color = color,
                textColor = textColor
            ) ?: createNewNote()
            note?.let { viewModel.saveChanges(it) }
        }
    }

    private fun createNewNote(): Note = Note(
        UUID.randomUUID().toString(),
        ui.titleEdit.text.toString(),
        ui.bodyTextEdit.text.toString(),
        color = color,
        textColor = textColor
    )

    override fun renderData(data: Data) {
        if (data.isRemoved) finish()

        this.note = data.note
        data.note?.let { color = it.color }
        initView()
    }

    private fun setEditListener() {
        ui.titleEdit.addTextChangedListener(textChangeListener)
        ui.bodyTextEdit.addTextChangedListener(textChangeListener)
    }

    private fun removeEditListener() {
        ui.titleEdit.removeTextChangedListener(textChangeListener)
        ui.bodyTextEdit.removeTextChangedListener(textChangeListener)
    }

    override fun onBackPressed() {
        if (ui.colorPicker.isOpen) {
            ui.colorPicker.close()
            return
        }
        super.onBackPressed()
    }
}