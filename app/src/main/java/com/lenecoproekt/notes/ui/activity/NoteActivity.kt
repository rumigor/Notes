package com.lenecoproekt.notes.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.ColorUtils
import com.lenecoproekt.notes.R
import com.lenecoproekt.notes.databinding.ActivityNoteBinding
import com.lenecoproekt.notes.model.Color
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.ui.base.BaseActivity
import com.lenecoproekt.notes.ui.getColorInt
import com.lenecoproekt.notes.viewmodel.NoteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private var titleColor: Color = Color.BLACK
    private val REQUESTSPEECHCODE = 100
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
                var newColor = it
                if (ui.titleEdit.isFocused) {
                    ui.titleEdit.setTextColor(it.getColorInt(this@NoteActivity))
                    ui.colorPicker.close()
                    titleColor = newColor
                }
                if (ui.bodyTextEdit.isFocused) {
                    ui.bodyTextEdit.setTextColor(it.getColorInt(this@NoteActivity))
                    ui.colorPicker.close()
                    textColor = newColor
                }
                triggerSaveNote()
            } else {
                color = it
                ui.titleEdit.setBackgroundColor(it.getColorInt(this@NoteActivity))
                ui.bodyTextEdit.setBackgroundColor(it.getColorInt(this@NoteActivity))
                ui.noteContainer.setBackgroundColor(it.getColorInt(this@NoteActivity))
//                setToolbarColor(it)
                ui.colorPicker.close()
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
            }
            if (note != ui.bodyTextEdit.text.toString()) {
                ui.bodyTextEdit.setText(note)
            }
            ui.titleEdit.setTextColor(titleColor.getColorInt(this@NoteActivity))
            ui.titleEdit.setBackgroundColor(color.getColorInt(this@NoteActivity))
            ui.bodyTextEdit.setTextColor(textColor.getColorInt(this@NoteActivity))
            ui.noteContainer.setBackgroundColor(color.getColorInt(this@NoteActivity))
            setEditListener()
            supportActionBar?.title = title
//            setToolbarColor(color)
        }
    }

    private fun setToolbarColor(color: Color) {
        val outHSL = FloatArray(3)
        val colorInt = color.getColorInt(this@NoteActivity)
        ui.toolbar.setBackgroundColor(colorInt)
        ColorUtils.colorToHSL(colorInt, outHSL)
        if (outHSL[2] > 0.8f) ui.toolbar.setTitleTextColor(
            resources.getColor(
                R.color.black,
                theme
            )
        )
        else ui.toolbar.setTitleTextColor(
            resources.getColor(
                R.color.white,
                theme
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
        menuInflater.inflate(R.menu.menu_note, menu).let { true }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> super.onBackPressed().let { true }
        R.id.palette -> togglePalette(false).let { true }
        R.id.textPalette -> togglePalette(true).let { true }
        R.id.delete -> removeNote().let { true }
        R.id.voice_to_text -> speak().let {true}
        else -> super.onOptionsItemSelected(item)
    }

    private fun speak() {
        val recordText = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recordText.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recordText.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        recordText.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please, speak something")

        try {
            startActivityForResult(recordText, REQUESTSPEECHCODE)

        } catch (e: Throwable){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUESTSPEECHCODE-> {
                if (resultCode == Activity.RESULT_OK && null != data){
                    var result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let{
                        if (ui.titleEdit.isFocused) {
                            ui.titleEdit.setText(result[0])
                        } else {
                            ui.bodyTextEdit.setText(result[0])
                        }
                    }
                    triggerSaveNote()
                }
            }
        }
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
                textColor = textColor,
                titleColor = titleColor
            ) ?: createNewNote()
            note?.let { viewModel.saveChanges(it) }
        }
    }

    private fun createNewNote(): Note = Note(
        UUID.randomUUID().toString(),
        ui.titleEdit.text.toString(),
        ui.bodyTextEdit.text.toString(),
        color = color,
        textColor = textColor,
        titleColor = titleColor
    )

    override fun renderData(data: Data) {
        if (data.isRemoved) finish()
        this.note = data.note
        data.note?.let { color = it.color }
        data.note?.let { textColor = it.textColor }
        data.note?.let { titleColor = it.titleColor}
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