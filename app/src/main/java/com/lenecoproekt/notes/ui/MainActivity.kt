package com.lenecoproekt.notes.ui

import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.lenecoproekt.notes.R
import com.lenecoproekt.notes.databinding.ActivityMainBinding
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.Repository
import com.lenecoproekt.notes.ui.base.BaseActivity
import com.lenecoproekt.notes.viewmodel.MainViewModel

class MainActivity : BaseActivity<List<Note>?, MainViewState>() {

    override val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    override val ui: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var adapter: MainAdapter

    override fun renderData(data: List<Note>?) {
        if (data == null) return
        adapter.notes = data

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(ui.toolbar)

        adapter = MainAdapter(object: OnItemClickListener{
            override fun onItemClick(note: Note) {
                openNoteScreen(note)
            }

        })
        ui.mainRecycler.adapter = adapter
        registerForContextMenu(ui.mainRecycler)

        ui.fab.setOnClickListener { openNoteScreen() }

        registerForContextMenu(ui.mainRecycler)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.update_context -> openNoteScreen(adapter.notes[adapter.position])

            R.id.remove_context -> {
                Repository.removeNote(adapter.notes[adapter.position].id)
                adapter.notifyDataSetChanged()
            }
            R.id.clear_context -> {

            }

        }
        return super.onContextItemSelected(item)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.context_menu, menu)
    }

    private fun openNoteScreen(note: Note? = null){
        startActivity(NoteActivity.getStartIntent(this, note?.id))
    }

}