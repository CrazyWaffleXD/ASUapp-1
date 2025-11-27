package com.example.asuapp001.utils

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseHelper(
    private val prefs: SharedPreferencesHelper,
    private val onDataUpdated: () -> Unit  // Меняем: просто вызов, без аргументов
) {
    private val database = Firebase.database
    private val adsRef = database.getReference("ads")  // Слушаем узел "ads"
    private var lastKnownCount = 0

    fun startListening() {
        // Загружаем последний известный счётчик
        lastKnownCount = prefs.getInt("ads_last_count", 0)

        adsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentCount = snapshot.childrenCount.toInt()

                // Если новых объявлений больше — показываем уведомление
                if (currentCount > lastKnownCount) {
                    prefs.saveInt("ads_last_count", currentCount)
                    onDataUpdated() // Уведомляем MainActivity
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseHelper", "Ошибка: ${error.message}")
            }
        })
    }
}