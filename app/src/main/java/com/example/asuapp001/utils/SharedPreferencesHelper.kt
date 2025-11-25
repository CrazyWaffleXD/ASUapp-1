package com.example.asuapp001.utils

// Импортируем Context и SharedPreferences — нужны для работы с настройками
import android.content.Context
import android.content.SharedPreferences

// Это наш класс-помощник для работы с SharedPreferences
// Назовём его SharedPreferencesHelper
class SharedPreferencesHelper private constructor(context: Context) {

    // --- ШАГ 1: СИнглтон (один экземпляр на всё приложение) ---

    // companion object — как "статические методы" в Java
    // Нужен, чтобы создать один единственный экземпляр (синглтон)
    companion object {
        // Имя файла, где будут храниться данные (как имя JSON-файла)
        private const val NAME = "MainActTable"
        // Режим доступа: только наше приложение может читать
        private const val MODE = Context.MODE_PRIVATE
        // Сам экземпляр (сначала null)
        private var INSTANCE: SharedPreferencesHelper? = null

        // Функция, чтобы получить экземпляр
        // context — нужно передать (например, this из Activity)
        fun getInstance(context: Context): SharedPreferencesHelper {
            // Если экземпляр ещё не создан — создаём
            return INSTANCE ?: synchronized(this) {
                val instance = SharedPreferencesHelper(context)
                INSTANCE = instance  // сохраняем
                instance             // возвращаем
            }
        }
    }

    // --- ШАГ 2: Объект SharedPreferences ---

    // Создаём сам объект SharedPreferences
    // context.applicationContext — чтобы контекст не "утёк" (memory leak)
    private val preferences: SharedPreferences = context.applicationContext.getSharedPreferences(NAME, MODE)

    // --- ШАГ 3: Полезные методы ---

    // Сохраняем строку по ключу
    fun saveString(key: String, value: String) {
        // edit() — начинаем редактирование
        // putString — добавляем пару ключ-значение
        // apply() — сохраняем асинхронно (без блокировки UI)
        preferences.edit().putString(key, value).apply()
    }

    // Получаем строку по ключу
    // Если нет — возвращаем значение по умолчанию
    fun getString(key: String, defaultValue: String): String? {
        return preferences.getString(key, defaultValue)
    }

    // Можно добавить и другие типы позже:
    // fun saveInt(key: String, value: Int) { ... }
    // fun getInt(key: String, default: Int): Int { ... }
}
