package com.example.asuapp001.ui.diaryPrilagusha

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.asuapp001.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.*

class diaryPrilagushaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private val notes = mutableListOf<Note>()
    private var adapter: NoteAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_slideshow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewNotes)
        fabAdd = view.findViewById(R.id.fabAddNote)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = NoteAdapter(notes) { saveNotes() }
        recyclerView.adapter = adapter

        loadNotes()

        fabAdd.setOnClickListener {
            addNewNote()
        }
    }

    private fun addNewNote() {
        val newNote = Note(title = "Новая заметка", content = "Введите текст...")
        notes.add(0, newNote)
        adapter?.notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
        saveNotes() // ✅ Без аргументов — всё верно
    }

    // Удалите эту функцию, если она не используется напрямую.
    // Сейчас она не нужна — удаление происходит в адаптере.
    // Если оставляете — убедитесь, что адаптер передаёт `onDeleteNote`
    // Но у вас сейчас используется `onNoteUpdated`, поэтому она лишняя.
    //
    // ❌ Закомментирована, так как дублирует логику
    // private fun onDeleteNote(note: Note) { ... }

    private fun loadNotes() {
        try {
            context?.openFileInput("notes.bin")?.use { input ->
                ObjectInputStream(input).use { ois ->
                    @Suppress("UNCHECKED_CAST")
                    val loaded = ois.readObject() as MutableList<Note>
                    notes.clear()
                    notes.addAll(loaded)
                }
            }
            adapter?.notifyDataSetChanged()
        } catch (e: Exception) {
            Toast.makeText(context, "Не удалось загрузить заметки", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun saveNotes() {
        try {
            context?.openFileOutput("notes.bin", Context.MODE_PRIVATE)?.use { output ->
                ObjectOutputStream(output).use { oos ->
                    oos.writeObject(notes.toMutableList()) // Сохраняем копию
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }
}