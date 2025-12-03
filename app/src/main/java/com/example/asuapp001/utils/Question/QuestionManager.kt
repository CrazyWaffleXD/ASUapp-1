package com.example.asuapp001.utils.Question

import android.widget.LinearLayout
import com.example.asuapp001.utils.Question.WebLinkCard

class QuestionManager(private val container: LinearLayout) {

    fun addQuestion(questionText: String, answerText: String) {
        ExpandableQuestion(container, questionText, answerText).create()
    }

    fun addWebLinkCard(container: LinearLayout, title: String, url: String, iconResId: Int){
        WebLinkCard(container, title, url, iconResId).create()
    }
}