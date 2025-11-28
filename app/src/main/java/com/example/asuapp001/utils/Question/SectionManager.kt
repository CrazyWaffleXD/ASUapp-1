package com.example.asuapp001.utils.Question

import android.graphics.Typeface
import android.widget.LinearLayout
import android.widget.TextView

class SectionManager(private val parentContainer: LinearLayout) {

    fun createSection(title: String): LinearLayout {
        // Заголовок раздела
        val sectionTitle = TextView(parentContainer.context).apply {
            text = title.uppercase()
            textSize = 16f
            setTextColor(0xFF1A4D99.toInt())
            typeface = Typeface.DEFAULT_BOLD
            setPadding(16, 32, 16, 8)
        }

        // Контейнер для вопросов этого раздела
        val sectionContainer = LinearLayout(parentContainer.context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Добавляем в родительский контейнер (например, общий ScrollView)
        parentContainer.addView(sectionTitle)
        parentContainer.addView(sectionContainer)

        return sectionContainer
    }
}