package com.example.asuapp001.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.example.asuapp001.R
import com.example.asuapp001.utils.Question.ExpandableQuestion
import com.example.asuapp001.utils.Question.SectionManager
import com.example.asuapp001.utils.Question.QuestionManager

class fragment_bopros : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bopros, container, false)

        val scrollView = view.findViewById<ScrollView>(R.id.scrollView)

        val sectionsContainer = view.findViewById<LinearLayout>(R.id.sections_container)
        val sectionManager = SectionManager(sectionsContainer)

        // === Раздел: Поступление ===
        val admissionContainer = sectionManager.createSection("Поступление")
        QuestionManager(admissionContainer).apply {
            addQuestion(
                "Как поступить в АлтГУ?",
                "Для поступления необходимо подать документы через приёмную комиссию, сдать ЕГЭ или внутренние экзамены, и подать заявление до 20 августа."
            )
            addQuestion(
                "Нужны ли оригиналы документов?",
                "Да, оригиналы необходимо предоставить после подачи копий для подтверждения."
            )
        }

        // === Раздел: Общежитие ===
        val hostelContainer = sectionManager.createSection("Общежитие")
        QuestionManager(hostelContainer).apply {
            addQuestion(
                "Есть ли общежитие для иногородних?",
                "Да, АлтГУ предоставляет общежитие для иногородних студентов. Необходимо подать заявление в отдел обеспечения жильём."
            )
            addQuestion(
                "Сколько стоит проживание?",
                "Проживание в общежитии бесплатное для студентов, обучающихся на бюджетной основе."
            )
        }

        // === Раздел: Расписание ===
        val scheduleContainer = sectionManager.createSection("Расписание")
        QuestionManager(scheduleContainer).apply {
            addQuestion(
                "Где найти расписание занятий?",
                "Расписание доступно на официальном сайте университета в разделе \"Студенту\" → \"Расписание\"."
            )
            addQuestion(
                "Можно ли получать расписание в Telegram?",
                "Да, бот АлтГУ присылает актуальное расписание по запросу."
            )
        }

        // === Раздел: Аркадий ===
        val ArkadyContainer = sectionManager.createSection("Важный вопрос")
        QuestionManager(ArkadyContainer).apply {
            addQuestion(
                "Аркадий жмурик?",
                "Да."
            )
        }

        return view
    }
}