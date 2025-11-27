package com.example.asuapp001.utils.Question

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.asuapp001.R

class ExpandableQuestion(
    private val container: ViewGroup,
    private val questionText: String,
    private val answerText: String
) {

    private fun dp(context: Context, value: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.resources.displayMetrics).toInt()
    }
    fun create() {
        val context = container.context

        // CardView
        val cardView = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).also { lp ->
                lp.bottomMargin = dp(context, 8f)
            }
            radius = dp(context, 8f).toFloat()
            cardElevation = dp(context, 4f).toFloat()
        }

        // Внутренний LinearLayout карточки
        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // Контейнер вопроса (с текстом и стрелкой)
        val questionContainer = LinearLayout(context).apply {
            id = View.generateViewId()
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(context, 16f), dp(context, 16f), dp(context, 16f), dp(context, 16f))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val question = TextView(context).apply {
            text = questionText
            textSize = 18f
            setTextColor(0xFF1A4D99.toInt())
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            )
        }

        // Стрелочка
        val arrow = ImageView(context).apply {
            id = View.generateViewId()
            setImageResource(R.drawable.ic_arrow_down)
            layoutParams = LinearLayout.LayoutParams(
                dp(context, 24f), dp(context, 24f)
            ).also { lp ->
                lp.marginStart = dp(context, 8f) // Небольшой отступ от текста
            }
        }

        questionContainer.addView(question)
        questionContainer.addView(arrow)

        // Ответ
        val answer = TextView(context).apply {
            id = View.generateViewId()
            text = answerText
            textSize = 16f
            setTextColor(0xFF333333.toInt())
            setPadding(dp(context, 8f), dp(context, 8f), dp(context, 8f), dp(context, 16f))
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
            ).also { lp ->
                lp.height = 0
                lp.marginStart = dp(context, 16f)
                lp.marginEnd = dp(context, 16f)
            }
        }

        linearLayout.addView(questionContainer)
        linearLayout.addView(answer)
        cardView.addView(linearLayout)
        container.addView(cardView)

        // Логика открытия/закрытия
        questionContainer.setOnClickListener {
            if (answer.visibility == View.GONE) {
                // Показать
                answer.visibility = View.VISIBLE
                answer.layoutParams.height = 0
                answer.requestLayout()

                answer.post {
                    val widthSpec = View.MeasureSpec.makeMeasureSpec(
                        container.measuredWidth - dp(context, 32f), // учитываем marginStart/End
                        View.MeasureSpec.AT_MOST
                    )
                    val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    answer.measure(widthSpec, heightSpec)

                    val targetHeight = answer.measuredHeight

                    val animator = ValueAnimator.ofInt(0, targetHeight)
                    animator.addUpdateListener { anim ->
                        answer.layoutParams.height = anim.animatedValue as Int
                        answer.requestLayout()
                    }
                    animator.duration = 300
                    animator.interpolator = DecelerateInterpolator()
                    animator.start()
                }

                arrow.animate().rotation(180f).setDuration(300).start()
            } else {
                // Скрыть
                val currentHeight = answer.measuredHeight
                val animator = ValueAnimator.ofInt(currentHeight, 0)
                animator.addUpdateListener { anim ->
                    answer.layoutParams.height = anim.animatedValue as Int
                    answer.requestLayout()
                }
                animator.duration = 300
                animator.interpolator = DecelerateInterpolator()
                animator.start()

                animator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        answer.visibility = View.GONE
                    }
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })

                arrow.animate().rotation(0f).setDuration(300).start()
            }
        }
    }
}