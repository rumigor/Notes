package com.lenecoproekt.notes.ui.activity

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.lenecoproekt.notes.R
import com.lenecoproekt.notes.databinding.ItemNoteBinding
import com.lenecoproekt.notes.model.Color
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.ui.getColorInt

interface OnItemClickListener {
    fun onItemClick(note: Note)
}

class MainAdapter(private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<MainAdapter.NoteViewHolder>() {
    var position = 0

    var notes: List<Note> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
        holder.itemView.setOnLongClickListener {
            this.position = holder.adapterPosition
            false
        }
    }


    override fun getItemCount(): Int = notes.size

    override fun onViewRecycled(holder: NoteViewHolder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder)
    }


    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ui: ItemNoteBinding = ItemNoteBinding.bind(itemView)

        fun bind(note: Note) {
            with(note) {
                ui.title.text = this.title
                ui.body.text = this.note
                ui.container.setCardBackgroundColor(this.color.getColorInt(itemView.context))
                itemView.setOnClickListener { onItemClickListener.onItemClick(this) }
            }
        }

    }

}