package com.lenecoproekt.notes.ui.activity

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
        if (noteId == null) supportActionBar?.title = getString(R.string.new_note_tilte)
        initView()
    }

    private fun initView() {

        note?.run {
            supportActionBar?.title = lastChanged.format()
            ui.titleEdit.setText(title)
            ui.bodyTextEdit.setText(note)
            ui.toolbar.setBackgroundColor(color.getColorInt(this@NoteActivity))
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
                note?.apply {
                    color = enumValues<Color>()[ui.spinner.selectedItemId.toInt()]
                }
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
                    enumValues<Color>()[ui.spinner.selectedItemId.toInt()]
                )
            if (note != null) viewModel.saveChanges(note!!)
        }, SAVE_DELAY)
    }

    override fun renderData(data: Note?) {
        this.note = data
        initView()
    }
}