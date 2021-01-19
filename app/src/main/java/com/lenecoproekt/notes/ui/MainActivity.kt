package com.lenecoproekt.notes.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
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
        adapter = MainAdapter()
        ui.mainRecycler.adapter = adapter

        viewModel.viewState().observe(this, { state ->
            state?.let { adapter.notes = state.notes }

        })
        ui.addNote.setOnClickListener {
            Repository.notes.add(
                Note(
                    "Новая добавленная заметка",
                    "Эта заметка добавлена с помощью кнопки",
                    0xfff01292.toInt()
                ),
            )
            adapter.notifyDataSetChanged()
        }
    }
}