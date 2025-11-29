package com.example.asuapp001.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.asuapp001.R
import com.example.asuapp001.utils.Question.ExpandableQuestion.Category
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class fragment_bopros : Fragment() {

    private lateinit var sectionsContainer: LinearLayout
    private val sectionHeaders = mutableListOf<TextView>()
    private val sectionContainers = mutableListOf<LinearLayout>()

    private var currentCategory: Category = Category.ALL  // Сохраняем текущую категорию

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bopros, container, false)

        val chipGroup = view.findViewById<ChipGroup>(R.id.filterGroup)
        sectionsContainer = view.findViewById(R.id.sections_container)

        createAllQuestions()

        // Настраиваем чипы с кликами
        setupChipClickListeners(chipGroup)

        // Изначально показываем "Все"
        filterSections(currentCategory)

        return view
    }

    private fun setupChipClickListeners(chipGroup: ChipGroup) {
        // Находим все чипы по ID
        val chips = listOf(
            chipGroup.findViewById<Chip>(R.id.btn_all),
            chipGroup.findViewById<Chip>(R.id.btn_app),
            chipGroup.findViewById<Chip>(R.id.btn_site),
            chipGroup.findViewById<Chip>(R.id.btn_docs)
        )

        chips.forEach { chip ->
            chip.setOnClickListener {
                val newCategory = when (chip.id) {
                    R.id.btn_all -> Category.ALL
                    R.id.btn_app -> Category.APP
                    R.id.btn_site -> Category.SITE
                    R.id.btn_docs -> Category.DOCUMENTS
                    else -> Category.ALL
                }

                // Всегда сбрасываем и устанавливаем выбранный
                chipGroup.clearCheck()
                chip.isChecked = true  // Визуально подсвечиваем

                // Применяем фильтр
                if (newCategory != currentCategory) {
                    currentCategory = newCategory
                }
                filterSections(currentCategory)
            }
        }
    }

    private fun createAllQuestions() {
        addSection("Помощь по приложению", Category.SITE) {
            addQuestion(
                it,
                "Как подать заявление на поступление?",
                "Лично в приёмную. Или на сайте АлтГУ. <a href='https://lk.abiturient.asu.ru/user/sign-in/login'>Подать заявление онлайн</a>"
            )
            addQuestion(
                it,
                "Нужны ли оригиналы документов?",
                "Да, для подтверждения.")
        }

        addSection("Общежитие", Category.APP) {
            addQuestion(it, "Есть ли общежитие для иногородних?", "Да, подайте заявление.")
            addQuestion(it, "Сколько стоит проживание?", "Бесплатно для бюджетников.")
        }

        addSection("Сайт", Category.SITE) {
            addQuestion(it, "Где найти расписание?", "На сайте в разделе \"Студенту\".")
        }

        addSection("Документы", Category.DOCUMENTS) {
            addQuestion(it, "Какие документы нужны?", "Паспорт, аттестат, СНИЛС.")
        }

        addSection("Важный вопрос", Category.ALL) {
            addQuestion(it, "Аркадий жмурик?", "Да.")
        }
    }

    private fun addSection(
        title: String,
        category: Category,
        block: (LinearLayout) -> Unit
    ) {
        val header = TextView(context).apply {
            text = title.uppercase()
            textSize = 16f
            setTextColor(resources.getColor(android.R.color.black, null))
            setPadding(16, 32, 16, 8)
            tag = category
        }

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            tag = category
        }

        sectionsContainer.addView(header)
        sectionsContainer.addView(container)

        sectionHeaders.add(header)
        sectionContainers.add(container)

        block(container)
    }

    private fun addQuestion(
        container: LinearLayout,
        question: String,
        answer: String
    ) {
        com.example.asuapp001.utils.Question.ExpandableQuestion(container, question, answer).create()
    }

    private fun filterSections(category: Category) {
        sectionHeaders.forEachIndexed { index, header ->
            val sectionCategory = header.tag as? Category ?: Category.ALL
            val isVisible = category == Category.ALL || category == sectionCategory

            sectionContainers[index].visibility = if (isVisible) View.VISIBLE else View.GONE
            header.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }
}