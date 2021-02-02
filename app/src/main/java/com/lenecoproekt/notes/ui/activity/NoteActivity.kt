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
import com.lenecoproekt.notes.viewmodel.NoteViewModel
import java.util.*


private const val SAVE_DELAY = 2000L

class NoteActivity : BaseActivity<NoteViewState.Data, NoteViewState>() {

    companion object {
        const val EXTRA_NOTE = "NoteActivity.extra.NOTE"
        fun getStartIntent(context: Context, noteId: String?): Intent {
            val intent = Intent(context, NoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE, noteId)
            return intent
        }
    }

    private var note: Note? = null
    private var color: Color = Color.YELLOW
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
        setSupportActionBar(ui.toolbar)
        val noteId = intent.getStringExtra(EXTRA_NOTE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        noteId?.let {
            viewModel.loadNote(it)
        } ?: run {
            supportActionBar?.title = "New note"
        }

        setEditListener()
    }

    private fun initView() {

        note?.run {
            supportActionBar?.title = lastChanged.format()
            ui.toolbar.setBackgroundColor(color.getColorInt(this@NoteActivity))

            removeEditListener()
            ui.titleEdit.setText(title)
            ui.bodyTextEdit.setText(note)
            setEditListener()
        }

        ui.titleEdit.addTextChangedListener(textChangeListener)
        ui.bodyTextEdit.addTextChangedListener(textChangeListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
        menuInflater.inflate(R.menu.menu_note, menu).let { true }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> super.onBackPressed().let { true }
//        R.id.palette -> tooglePalette().let { true }
        R.id.delete -> removeNote().let { true }
        else -> super.onOptionsItemSelected(item)
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

        Handler(Looper.getMainLooper()).postDelayed({
            note = note?.copy(
                title = ui.titleEdit.text.toString(),
                note = ui.bodyTextEdit.text.toString(),
                lastChanged = Date(),
            )
                ?: viewModel.createNewNote(
                    ui.titleEdit.text.toString(),
                    ui.bodyTextEdit.text.toString(),
                    color = color
                )
            if (note != null) viewModel.saveChanges(note!!)
        }, SAVE_DELAY)
    }

    override fun renderData(data: NoteViewState.Data) {
        if (data.isRemoved) finish()
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
}