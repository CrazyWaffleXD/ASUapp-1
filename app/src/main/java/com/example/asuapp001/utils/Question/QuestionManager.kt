package com.example.asuapp001.utils.Question

import android.widget.LinearLayout

class QuestionManager(private val container: LinearLayout) {  // ← Теперь принимает LinearLayout

    fun addQuestion(questionText: String, answerText: String) {
        ExpandableQuestion(container, questionText, answerText).create()
    }

    fun addQuestions(questions: List<Pair<String, String>>) {
        for ((question, answer) in questions) {
            addQuestion(question, answer)
        }
    }
}