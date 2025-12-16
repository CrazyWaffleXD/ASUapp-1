package com.example.asuapp001

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // Включаем оффлайн-кэширование ДО первого обращения к Firebase
        FirebaseDatabase.getInstance("https://asuapp-978f2-default-rtdb.firebaseio.com/").setPersistenceEnabled(true)
    }
}