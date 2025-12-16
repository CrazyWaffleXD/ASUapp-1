package com.example.asuapp001.ui.diaryPrilagusha

import java.io.Serializable

data class Note(
    var title: String = "",
    var content: String = ""
) : Serializable {
    private companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }
}