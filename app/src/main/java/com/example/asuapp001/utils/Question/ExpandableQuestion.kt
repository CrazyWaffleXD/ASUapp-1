package com.example.asuapp001.utils.Question

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.asuapp001.R
import com.example.asuapp001.utils.Question.ChangeDp.dp

class ExpandableQuestion(
    private val container: ViewGroup,
    private val questionText: String,
    private val answerText: String
) {

    enum class Category {
        ALL, APP, SITE, DOCUMENTS
    }

    fun create(): View {
        val context = container.context

        val cardView = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).also { lp ->
                lp.bottomMargin = context.dp(8f)
            }
            radius = context.dp(8f).toFloat()
            cardElevation = context.dp(4f).toFloat()
            setCardBackgroundColor(context.getColor(R.color.white))
        }

        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        val questionContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(
                context.dp(16f),
                context.dp(16f),
                context.dp(16f),
                context.dp(16f)
            )
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val question = TextView(context).apply {
            text = questionText
            textSize = 18f
            setTextColor(context.getColor(R.color.purple_700))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            )
        }

        val arrow = ImageView(context).apply {
            setImageResource(R.drawable.ic_arrow_down)
            layoutParams = LinearLayout.LayoutParams(
                context.dp(24f), context.dp(24f)
            ).also { lp ->
                lp.marginStart = context.dp(8f)
            }
        }

        questionContainer.addView(question)
        questionContainer.addView(arrow)

        val answer = TextView(context).apply {
            val htmlText = answerText
            text = android.text.Html.fromHtml(htmlText, android.text.Html.FROM_HTML_MODE_COMPACT)
            setLinkTextColor(context.getColor(R.color.purple_200))
            movementMethod = android.text.method.LinkMovementMethod.getInstance()
            textSize = 16f
            setTextColor(0xFF333333.toInt())
            setPadding(
                context.dp(16f),
                context.dp(8f),
                context.dp(16f),
                context.dp(8f)
            )
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
            ).also { lp ->
                lp.height = 0
                lp.marginStart = context.dp(16f)
                lp.marginEnd = context.dp(16f)
                lp.bottomMargin = context.dp(16f)
            }
        }

        linearLayout.addView(questionContainer)
        linearLayout.addView(answer)
        cardView.addView(linearLayout)
        container.addView(cardView)

        cardView.tag = mapOf("answer" to answer, "arrow" to arrow)

        questionContainer.setOnClickListener {
            val data = cardView.tag as? Map<String, View> ?: return@setOnClickListener
            val answerView = data["answer"] as? TextView ?: return@setOnClickListener
            val arrowView = data["arrow"] as? ImageView ?: return@setOnClickListener

            if (answerView.visibility == View.GONE) {
                showAnswer(answerView, arrowView)
            } else {
                hideAnswer(answerView, arrowView)
            }
        }

        return cardView
    }

    private fun showAnswer(answer: TextView, arrow: ImageView) {
        answer.visibility = View.VISIBLE
        answer.measure(
            View.MeasureSpec.makeMeasureSpec(answer.parentWidth() - answer.context.dp(32f), View.MeasureSpec.AT_MOST),
            View.MeasureSpec.UNSPECIFIED
        )
        val targetHeight = answer.measuredHeight

        val animator = ValueAnimator.ofInt(0, targetHeight)
        animator.addUpdateListener { anim ->
            answer.layoutParams.height = anim.animatedValue as Int
            answer.requestLayout()
        }
        animator.duration = 200
        animator.interpolator = DecelerateInterpolator()
        animator.start()
        arrow.animate().rotation(180f).setDuration(300).start()
    }

    private fun hideAnswer(answer: TextView, arrow: ImageView) {
        val currentHeight = answer.measuredHeight
        val animator = ValueAnimator.ofInt(currentHeight, 0)
        animator.addUpdateListener { anim ->
            answer.layoutParams.height = anim.animatedValue as Int
            answer.requestLayout()
        }
        animator.duration = 200
        animator.interpolator = DecelerateInterpolator()
        animator.start()

        animator.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {}
            override fun onAnimationEnd(animation: android.animation.Animator) {
                answer.visibility = View.GONE
            }
            override fun onAnimationCancel(animation: android.animation.Animator) {}
            override fun onAnimationRepeat(animation: android.animation.Animator) {}
        })
        arrow.animate().rotation(0f).setDuration(300).start()
    }

    private fun View.parentWidth(): Int {
        val p = parent
        return if (p is View) p.measuredWidth else 0
    }
}