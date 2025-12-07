package com.example.asuapp001.ui

import android.animation.*
import android.os.Bundle
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

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.GenericTypeIndicator

class FragmentQuestion : Fragment() {

    private lateinit var sectionsContainer: LinearLayout
    private val sectionHeaders = mutableListOf<TextView>()
    private val sectionContainers = mutableListOf<LinearLayout>()
    private val db = FirebaseDatabase.getInstance("https://asuapp-978f2-default-rtdb.firebaseio.com/").reference

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

        db.child("test").setValue("Hello from ASU!")

        loadQuestionsFromFirebase() // Загружаем из Firebase

        return view
    }
    private fun loadQuestionsFromFirebase() {
        val sectionsRef = db.child("questions_data").child("sections")

        sectionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sectionsList = mutableListOf<Pair<String, Map<String, Any?>>>()

                for (child in snapshot.children) {
                    val key = child.key ?: continue
                    if (!key.startsWith("section_")) continue

                    val sectionData = child.getValue(object : GenericTypeIndicator<Map<String, Any?>>() {})
                        ?: continue

                    sectionsList.add(key to sectionData)
                }

                val sortedSections = sectionsList.sortedBy { it.first }

                sectionsContainer.removeAllViews()
                sectionHeaders.clear()
                sectionContainers.clear()

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
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Ошибка загрузки: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
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