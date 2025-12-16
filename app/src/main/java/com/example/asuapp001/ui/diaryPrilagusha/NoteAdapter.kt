package com.example.asuapp001.ui.diaryPrilagusha

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.asuapp001.R

class NoteAdapter(
    private val notes: MutableList<Note>,
    private val onNoteUpdated: () -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: EditText = itemView.findViewById(R.id.textViewTitle)
        val textViewContent: EditText = itemView.findViewById(R.id.textViewContent)
        val textViewClose: TextView = itemView.findViewById(R.id.textViewClose)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
    val note = notes[position]

    holder.textViewTitle.setText(note.title)
    holder.textViewContent.setText(note.content)

    holder.textViewTitle.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            note.title = (v as EditText).text.toString()
            onNoteUpdated()
        }
    }

    holder.textViewContent.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            note.content = (v as EditText).text.toString()
            onNoteUpdated()
        }
    }

    holder.textViewClose.setOnClickListener {
        // ❌ Опасно: position может быть устаревшей
        // ✅ Используем актуальную позицию
        val pos = holder.adapterPosition
        if (pos != RecyclerView.NO_POSITION) {
            notes.removeAt(pos)
            notifyItemRemoved(pos)
            onNoteUpdated()
        }
    }
}

    override fun getItemCount() = notes.size
}