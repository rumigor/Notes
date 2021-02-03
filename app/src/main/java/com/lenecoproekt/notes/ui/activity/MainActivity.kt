package com.lenecoproekt.notes.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import com.firebase.ui.auth.AuthUI
import com.lenecoproekt.notes.R
import com.lenecoproekt.notes.databinding.ActivityMainBinding
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.ui.base.BaseActivity
import com.lenecoproekt.notes.viewmodel.MainViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<List<Note>?, MainViewState>(), LogoutDialog.LogoutListener {

    override val viewModel: MainViewModel by viewModel()
    override val ui: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var adapter: MainAdapter

    override fun renderData(data: List<Note>?) {
        data?.let {
            adapter.notes = data
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(ui.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = (viewModel.requestUser().value?.name + " notes")
        adapter = MainAdapter(object : OnItemClickListener {
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
        when (item.itemId) {
            R.id.update_context -> openNoteScreen(adapter.notes[adapter.position])

            R.id.remove_context -> {
                viewModel.removeNote(adapter.notes[adapter.position].id)
                adapter.notifyDataSetChanged()
            }
            R.id.clear_context -> {
                viewModel.deleteAllNotes()
                adapter.notifyDataSetChanged()
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

    private fun openNoteScreen(note: Note? = null) {
        startActivity(NoteActivity.getStartIntent(this, note?.id))
    }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
        MenuInflater(this).inflate(R.menu.menu_main, menu).let { true }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.logout -> showLogoutDialog().let { true }
            else -> false
        }

    private fun showLogoutDialog() {
        supportFragmentManager.findFragmentByTag(LogoutDialog.TAG) ?: LogoutDialog.createInstance()
            .show(supportFragmentManager, LogoutDialog.TAG)
    }

    override fun onLogout() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
    }
}