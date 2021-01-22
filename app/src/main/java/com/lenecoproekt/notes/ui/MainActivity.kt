package com.lenecoproekt.notes.ui

import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lenecoproekt.notes.R
import com.lenecoproekt.notes.databinding.ActivityMainBinding
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.Repository
import com.lenecoproekt.notes.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    lateinit var ui: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    lateinit var adapter: MainAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)
        setSupportActionBar(ui.toolbar)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        adapter = MainAdapter(object: OnItemClickListener{
            override fun onItemClick(note: Note) {
                openNoteScreen(note)
            }

        })
        ui.mainRecycler.adapter = adapter
        registerForContextMenu(ui.mainRecycler)
        viewModel.viewState().observe(this, { state ->
            state?.let { adapter.notes = state.notes }

        })

        ui.fab.setOnClickListener { openNoteScreen() }

//        registerForContextMenu(ui.mainRecycler)
    }

//    override fun onContextItemSelected(item: MenuItem): Boolean {
//        when (item.itemId){
//            R.id.update_context -> Toast.makeText(
//                this,
//                "Функция будет реализована позже",
//                Toast.LENGTH_SHORT
//            ).show()
//            R.id.remove_context -> {
//                Repository.notes.removeAt(adapter.position)
//                adapter.notifyDataSetChanged()
//            }
//            R.id.clear_context -> {
//                Repository.notes.clear()
//                adapter.notifyDataSetChanged()
//            }
//
//        }
//        return super.onContextItemSelected(item)
//    }
//
//    override fun onCreateContextMenu(
//        menu: ContextMenu?,
//        v: View?,
//        menuInfo: ContextMenu.ContextMenuInfo?
//    ) {
//        super.onCreateContextMenu(menu, v, menuInfo)
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.context_menu, menu)
//    }

    private fun openNoteScreen(note: Note? = null){
        startActivity(NoteActivity.getStartIntent(this, note))
    }

}