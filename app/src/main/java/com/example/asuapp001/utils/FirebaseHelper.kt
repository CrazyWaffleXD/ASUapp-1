package com.example.asuapp001.utils

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseHelper(
    private val prefs: SharedPreferencesHelper,
    private val onDataUpdated: (String) -> Unit
) {
    private val database = Firebase.database
    private val myRef = database.getReference("message")
    private var currentValue: String = ""

    fun startListening() {
        // Загружаем последнее сохранённое значение
        currentValue = prefs.getString("dataMainValue", "ds") ?: "ds"

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Явно получаем значение как String
                val value = snapshot.getValue(String::class.java)

                if (value != null && value != currentValue) {
                    currentValue = value
                    prefs.saveString("dataMainValue", value)
                    onDataUpdated(value) // Уведомляем MainActivity
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseHelper", "Firebase database error: ${error.message}")
            }
        })
    }
}