package com.lenecoproekt.notes.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lenecoproekt.notes.R
import com.lenecoproekt.notes.databinding.ItemNoteBinding
import com.lenecoproekt.notes.model.Note

class MainAdapter : RecyclerView.Adapter<MainAdapter.NoteViewHolder>() {
//    lateinit var mOnLongItemClickListener: OnLongItemClickListener
    var notes: List<Note> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
//    fun setOnLongItemClickListener(onLongItemClickListener: OnLongItemClickListener) {
//        mOnLongItemClickListener = onLongItemClickListener
//    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
//        holder.itemView.setOnLongClickListener { v ->
//            if (mOnLongItemClickListener != null) {
//                mOnLongItemClickListener.itemLongClicked(v, position)
//            }
//            true
//        }
    }

    override fun getItemCount(): Int = notes.size

//    interface OnLongItemClickListener {
//        fun itemLongClicked(v: View?, position: Int)
//    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val ui: ItemNoteBinding = ItemNoteBinding.bind(itemView)

        fun bind(note: Note){
            with(note){
                ui.title.text = this.title
                ui.body.text = this.note
                itemView.setBackgroundColor(this.color)
            }
        }

    }

}