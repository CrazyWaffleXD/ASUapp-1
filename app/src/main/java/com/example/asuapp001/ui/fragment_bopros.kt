package com.example.asuapp001.ui

import android.animation.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.asuapp001.R
import com.example.asuapp001.utils.Question.ExpandableQuestion.Category
import com.example.asuapp001.utils.Question.QuestionManager
import com.example.asuapp001.utils.Question.WebLinkCard
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class fragment_bopros : Fragment() {

    private lateinit var sectionsContainer: LinearLayout
    private val sectionHeaders = mutableListOf<TextView>()
    private val sectionContainers = mutableListOf<LinearLayout>()
    private val db = Firebase.firestore // или FirebaseFirestore.getInstance()

    private var currentCategory: Category = Category.ALL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bopros, container, false)

        val chipGroup = view.findViewById<ChipGroup>(R.id.filterGroup)
        sectionsContainer = view.findViewById(R.id.sections_container)

        setupChipClickListeners(chipGroup)

        loadQuestionsFromFirebase() // Загружаем из Firebase

        return view
    }
    private fun loadQuestionsFromFirebase() {
        db.collection("questions_data").document("sections")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val sections = document.data?.entries
                        ?.filter { it.key.startsWith("section_") }
                        ?.sortedBy { it.key } // по порядку

                    sections?.forEach { (_, sectionData) ->
                        val title = sectionData["title"] as? String ?: return@forEach
                        val categoryStr = sectionData["category"] as? String ?: "ALL"
                        val category = Category.valueOf(categoryStr)

                        val questions = sectionData["questions"] as? List<Map<String, String>> ?: emptyList()
                        val links = sectionData["links"] as? List<Map<String, String>> ?: emptyList()

                        addSection(title, category) { container ->
                            // Добавляем вопросы
                            questions.forEach { q ->
                                addQuestion(
                                    container,
                                    q["question"] ?: "",
                                    q["answer"] ?: ""
                                )
                            }

                            // Добавляем ссылки
                            links.forEach { link ->
                                addWebLinkCard(
                                    container = container,
                                    title = link["title"] ?: "",
                                    url = link["url"] ?: ""
                                )
                            }
                        }
                    }

                    // Фильтруем после загрузки
                    filterSections(currentCategory)
                }
            }
            .addOnFailureListener { exception ->
                // Обработка ошибки (нет сети, нет данных и т.п.)
                // Можно показать toast или заглушку
                android.widget.Toast.makeText(context, "Ошибка загрузки: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupChipClickListeners(chipGroup: ChipGroup) {
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

                chipGroup.clearCheck()
                chip.isChecked = true

                if (newCategory != currentCategory) {
                    currentCategory = newCategory
                }
                filterSections(currentCategory)
            }
        }
    }

    private fun createAllQuestions() {
        addSection("Помощь по приложению", Category.APP) {
            addQuestion(
                it,
                "Методические рекомендации",
                "В разработке..."
            )
            addQuestion(
                it,
                "Документация",
                "В разработке..."
            )
            addQuestion(
                it,
                "Не приходит уведомление",
                "Проверьте настройки уведомлений в приложении и на телефоне. Разрешите все типы уведомлений для этого приложения."
            )
        }

        addSection("Полезные ссылки", Category.ALL) { container ->
            addWebLinkCard(
                container = container,
                title = "Сайт АлтГУ",
                url = "https://asu.ru"
            )
        }

        addSection("Помощь по сайту", Category.SITE) {
            addQuestion(
                it,
                "Подать заявление на поступление",
                "Заявления можно подать в электронном виде <a href='https://pk.asu.ru'>на сайте АлтГУ</a>."
            )
            addQuestion(
                it,
                "Как зарегистрироваться?",
                "Зарегистрироваться можно через личный кабинет студента на сайте <a href='https://lk.asu.ru'>ЛК АСУ</a>."
            )
            addQuestion(
                it,
                "Где найти список преподавателей?",
                "На сайте в разделе <a href='https://www.asu.ru/sveden/employees/#lecturers'>Сведения об образовательной организации/Руководство и педагогический состав</a>."
            )
            addQuestion(
                it,
                "Как сменить пароль?",
                "Смена пароля осуществляется в личном кабинете на сайте <a href='https://lk.asu.ru'>ЛК АСУ</a>."
            )
        }

        addSection("Документы", Category.DOCUMENTS) {
            addQuestion(
                it,
                "Какие документы нужны абитуриенту?",
                "Паспорт, аттестат с приложением, СНИЛС, ИНН, фото 3×4, медицинская справка (по необходимости)."
            )
            addQuestion(
                it,
                "Можно ли заказать справку онлайн?",
                "Да, <a href='https://www.asu.ru/univer_about/uslugi/stud_usl/'>на сайте в разделе Об органиации/МФЦ</a>."
            )
        }

        addSection("Обратная связь", Category.APP) {
            addQuestion(
                it,
                "Техническая поддержка",
                "Пишите на email: <a href='mailto:support@asu.ru'>support@asu.ru</a>"
            )
            addQuestion(
                it,
                "Telegram-бот",
                "Наш бот поможет: <a href='https://t.me/ASU_SupportBot'>@ASU_SupportBot</a>"
            )
        }

        addSection("Образовательные ресурсы и сообщества АлтГУ", Category.SITE) {
            addQuestion(
                it,
                "Официальный сайт",
                "<a href='https://www.asu.ru'>www.asu.ru</a>")
            addQuestion(
                it,
                "Личный кабинет",
                "<a href='https://lk.asu.ru'>lk.asu.ru</a>")
            addQuestion(
                it,
                "Telegram-бот",
                "Наш бот поможет: <a href='https://t.me/ASU_SupportBot'>@ASU_SupportBot</a>"
            )
            addQuestion(
                it,
                "Сообщества",
                "<a href=\"https://max.ru/id2225004738_biz\" target=\"_new\"> Max</a><br>" +
                        "<a href=\"https://vk.com/public127455995\" target=\"_new\"> ВКонтакте</a><br>" +
                        "<a href=\"https://ok.ru/group/64178468815086\" target=\"_new\"> Одноклассники</a><br>" +
                        "<a href=\"https://youtube.com/c/AsuRuUniversity/\" target=\"_new\"> YouTube</a><br>" +
                        "<a href=\"https://zen.yandex.ru/id/5e72e617b894223da526fae6\" target=\"_new\"> Яндекс Дзен</a><br>" +
                        "<a href=\"https://rutube.ru/channel/23478960/\" target=\"_new\"> Rutube</a><br>" +
                        "<a href=\"https://t.me/asuinfo\" target=\"_new\"> Telegram</a>"
            )

        }

        addSection("О приложении", Category.ALL) {
            addQuestion(
                it,
                "Версия приложения",
                "v1.0.0 (2025)"
            )
            addQuestion(
                it,
                "Политика конфиденциальности",
                "<a href='https://www.asu.ru/privacy'>Ознакомьтесь с политикой обработки персональных данных</a>"
            )
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

    private fun addWebLinkCard(
        container: LinearLayout,
        title: String,
        url: String,
        iconResId: Int? = null
    ) {
        WebLinkCard(container, title, url, iconResId).create()
    }

    private fun filterSections(category: Category) {
        val toHide = mutableListOf<Pair<TextView, LinearLayout>>()
        val toShow = mutableListOf<Pair<TextView, LinearLayout>>()

        sectionHeaders.forEachIndexed { index, header ->
            val container = sectionContainers[index]
            val sectionCategory = header.tag as? Category ?: Category.ALL
            val isVisible = category == Category.ALL || category == sectionCategory

            if (isVisible && container.visibility == View.GONE) {
                toShow.add(header to container)
            } else if (!isVisible && container.visibility == View.VISIBLE) {
                toHide.add(header to container)
            }
        }

        // Сначала скрываем
        if (toHide.isEmpty()) {
            // Если нечего скрывать — сразу показываем
            toShow.forEach { (h, c) -> animateExpand(c, h) }
        } else {
            // Храним счётчик завершённых анимаций
            var completed = 0
            toHide.forEach { (header, container) ->
                animateCollapse(container, header) {
                    completed++
                    if (completed == toHide.size) {
                        // После всех исчезновений — показываем нужные
                        toShow.forEach { (h, c) -> animateExpand(c, h) }
                    }
                }
            }
        }
    }
    private fun animateExpand(container: LinearLayout, header: TextView) {
        header.apply {
            alpha = 0f
            visibility = View.VISIBLE
        }
        container.apply {
            alpha = 0f
            visibility = View.VISIBLE
        }

        // Анимация появления
        val headerAnimator = ObjectAnimator.ofFloat(header, "alpha", 1f)
        val containerAnimator = ObjectAnimator.ofFloat(container, "alpha", 1f)

        AnimatorSet().apply {
            playTogether(headerAnimator, containerAnimator)
            duration = 300
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    private fun animateCollapse(container: LinearLayout, header: TextView, onEnd: () -> Unit) {
        val headerAnimator = ObjectAnimator.ofFloat(header, "alpha", 0f)
        val containerAnimator = ObjectAnimator.ofFloat(container, "alpha", 0f)

        AnimatorSet().apply {
            playTogether(headerAnimator, containerAnimator)
            duration = 300
            interpolator = DecelerateInterpolator()
            start()

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    header.visibility = View.GONE
                    container.visibility = View.GONE
                    header.alpha = 1f
                    container.alpha = 1f
                    onEnd() // Вызываем после анимации
                }
            })
        }
    }

    private fun getContainerHeight(container: LinearLayout): Int {
        var totalHeight = 0
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            child.measure(
                View.MeasureSpec.makeMeasureSpec(container.measuredWidth, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.UNSPECIFIED
            )
            totalHeight += child.measuredHeight
        }
        return totalHeight
    }
}