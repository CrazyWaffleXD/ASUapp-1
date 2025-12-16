package com.example.asuapp001.ui

import android.animation.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.asuapp001.R
import com.example.asuapp001.utils.Question.ExpandableQuestion.Category
import com.example.asuapp001.utils.Question.WebLinkCard
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.*

class FragmentQuestion : Fragment() {

    private lateinit var sectionsContainer: LinearLayout
    private val sectionHeaders = mutableListOf<TextView>()
    private val sectionContainers = mutableListOf<LinearLayout>()
    private val db = FirebaseDatabase.getInstance("https://asuapp-978f2-default-rtdb.firebaseio.com/").reference

    private var currentCategory: Category = Category.ALL

    companion object {
        private const val TAG = "FragmentQuestion"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: Начало инициализации фрагмента")

        val view = inflater.inflate(R.layout.fragment_bopros, container, false)

        val chipGroup = view.findViewById<ChipGroup>(R.id.filterGroup)
        sectionsContainer = view.findViewById(R.id.sections_container)

        setupChipClickListeners(chipGroup)

        // Включаем синхронизацию узла
        db.child("questions_data").child("sections").keepSynced(true)
        Log.d(TAG, "keepSynced(true) установлен для questions_data/sections")

        // Тестовое значение
        db.child("test").setValue("Hello from ASU! at ${System.currentTimeMillis()}")
            .addOnSuccessListener {
                Log.d(TAG, "Тестовое значение успешно записано в Firebase")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Ошибка записи в Firebase: ${e.message}")
            }

        loadQuestionsFromFirebase()

        return view
    }

    private fun loadQuestionsFromFirebase() {
        Log.d(TAG, "loadQuestionsFromFirebase: Запуск загрузки данных")
        val sectionsRef = db.child("questions_data").child("sections")

        val cacheListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: Получены данные из Firebase")
                Log.d(TAG, "Количество детей в snapshot: ${snapshot.childrenCount}")
                Log.d(TAG, "Есть ли данные? ${snapshot.exists()}")

                if (snapshot.exists()) {
                    parseAndShowData(snapshot, isFromCache = true)
                    fetchFromServer()
                } else {
                    Log.d(TAG, "snapshot.exists() == false → данных нет ни в кэше, ни в сети")
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Нет данных. Подключитесь к интернету.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: Ошибка Firebase: ${error.message}, код: ${error.code}")
                when (error.code) {
                    DatabaseError.DISCONNECTED -> Log.w(TAG, "Firebase: нет подключения к сети")
                    DatabaseError.NETWORK_ERROR -> Log.w(TAG, "Firebase: сетевая ошибка")
                    DatabaseError.PERMISSION_DENIED -> Log.e(TAG, "Firebase: доступ запрещён (проверь rules)")
                    else -> Log.e(TAG, "Прочая ошибка: ${error.details}")
                }

                activity?.runOnUiThread {
                    if (sectionsContainer.childCount == 0) {
                        Toast.makeText(context, "Нет интернета. Данные недоступны.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        Log.d(TAG, "addListenerForSingleValueEvent: регистрируем слушатель (кэш или сеть)")
        sectionsRef.addListenerForSingleValueEvent(cacheListener)
    }

    private fun fetchFromServer() {
        Log.d(TAG, "fetchFromServer: Запрос свежих данных с сервера")
        val sectionsRef = db.child("questions_data").child("sections")

        val serverListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "serverListener: onDataChange — получены свежие данные с сервера")
                Log.d(TAG, "Количество секций: ${snapshot.childrenCount}")

                if (snapshot.exists()) {
                    parseAndShowData(snapshot, isFromCache = false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "serverListener: Ошибка при загрузке с сервера: ${error.message}")
                Toast.makeText(context, "Ошибка обновления: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }

        val listenerRef = sectionsRef.addValueEventListener(serverListener)
        Log.d(TAG, "addValueEventListener: временный слушатель добавлен для принудительного обновления")

        // Удаляем после первого вызова
        sectionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sectionsRef.removeEventListener(serverListener)
                Log.d(TAG, "serverListener: удалён после первого обновления")
            }

            override fun onCancelled(error: DatabaseError) {
                sectionsRef.removeEventListener(serverListener)
                Log.e(TAG, "serverListener: удалён из-за ошибки: ${error.message}")
            }
        })
    }

    private fun parseAndShowData(snapshot: DataSnapshot, isFromCache: Boolean) {
        Log.d(TAG, "parseAndShowData: парсинг данных, источник = ${if (isFromCache) "кэш" else "сеть"}")

        val sectionsList = mutableListOf<Pair<String, Map<String, Any?>>>()
        for (child in snapshot.children) {
            val key = child.key ?: continue
            if (!key.startsWith("section_")) continue

            val sectionData = child.getValue(object : GenericTypeIndicator<Map<String, Any?>>() {}) ?: continue
            sectionsList.add(key to sectionData)
        }

        Log.d(TAG, "Найдено секций: ${sectionsList.size}")
        if (sectionsList.isEmpty()) {
            Log.w(TAG, "parseAndShowData: данные есть, но секции не найдены — возможно, структура отличается")
        }

        val sortedSections = sectionsList.sortedBy { it.first }

        activity?.runOnUiThread {
            Log.d(TAG, "UI: Обновление интерфейса, isFromCache = $isFromCache")

            if (!isFromCache) {
                sectionsContainer.removeAllViews()
                sectionHeaders.clear()
                sectionContainers.clear()
                Log.d(TAG, "UI: Контейнеры очищены перед обновлением")
            }

            for ((_, sectionData) in sortedSections) {
                val title = sectionData["title"] as? String ?: "Без названия"
                val categoryStr = sectionData["category"] as? String ?: "ALL"
                val category = runCatching { Category.valueOf(categoryStr) }.getOrNull() ?: Category.ALL

                @Suppress("UNCHECKED_CAST")
                val questions = (sectionData["questions"] as? List<Map<String, String>>) ?: emptyList()

                @Suppress("UNCHECKED_CAST")
                val links = (sectionData["links"] as? List<Map<String, String>>) ?: emptyList()

                addSection(title, category) { container ->
                    questions.forEach { q ->
                        addQuestion(
                            container,
                            q["question"] ?: "",
                            q["answer"] ?: ""
                        )
                    }
                    links.forEach { link ->
                        addWebLinkCard(
                            container = container,
                            title = link["title"] ?: "",
                            url = link["url"] ?: ""
                        )
                    }
                }
            }

            filterSections(currentCategory)
            Log.d(TAG, "UI: Интерфейс обновлён, фильтрация применена")
        }
    }

    // ... остальные функции (setupChipClickListeners, addSection, filterSections и т.д.) без изменений
    // (они не влияют на загрузку, но оставлю ниже для полноты)

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

        if (toHide.isEmpty()) {
            toShow.forEach { (h, c) -> animateExpand(c, h) }
        } else {
            var completed = 0
            toHide.forEach { (header, container) ->
                animateCollapse(container, header) {
                    completed++
                    if (completed == toHide.size) {
                        toShow.forEach { (h, c) -> animateExpand(c, h) }
                    }
                }
            }
        }
    }

    private fun animateExpand(container: LinearLayout, header: TextView) {
        header.apply { alpha = 0f; visibility = View.VISIBLE }
        container.apply { alpha = 0f; visibility = View.VISIBLE }

        val headerAnimator = ObjectAnimator.ofFloat(header, "alpha", 1f)
        val containerAnimator = ObjectAnimator.ofFloat(container, "alpha", 1f)

        AnimatorSet().apply {
            playTogether(headerAnimator, containerAnimator)
            duration = 200
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    private fun animateCollapse(container: LinearLayout, header: TextView, onEnd: () -> Unit) {
        val headerAnimator = ObjectAnimator.ofFloat(header, "alpha", 0f)
        val containerAnimator = ObjectAnimator.ofFloat(container, "alpha", 0f)

        AnimatorSet().apply {
            playTogether(headerAnimator, containerAnimator)
            duration = 200
            interpolator = DecelerateInterpolator()
            start()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    header.visibility = View.GONE
                    container.visibility = View.GONE
                    header.alpha = 1f
                    container.alpha = 1f
                    onEnd()
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