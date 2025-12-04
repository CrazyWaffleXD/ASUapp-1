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

class WebLinkCard(
    private val container: LinearLayout,
    private val title: String,
    private val url: String,
    private val iconResId: Int? = null
) {

    fun create(): View {
        val context = container.context
        val card = CardView(context).apply {
            val padding = context.dp(16f)
            setContentPadding(padding, padding, padding, padding)
            radius = context.dp(8f).toFloat()
            elevation = context.dp(2f).toFloat()
            setCardBackgroundColor(0xFFFFFFFF.toInt())
            setOnClickListener {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                    data = android.net.Uri.parse(url)
                }
                context.startActivity(intent)
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(
                context.dp(16f),
                context.dp(12f),
                context.dp(16f),
                context.dp(12f)
            )
        }

        if (iconResId != null) {
            val icon = ImageView(context).apply {
                setImageResource(iconResId)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                adjustViewBounds = true
                layoutParams = LinearLayout.LayoutParams(
                    context.dp(24f),
                    context.dp(24f)
                ).also { lp ->
                    lp.marginEnd = context.dp(16f)
                }
            }
            linearLayout.addView(icon)
        }

        val textView = TextView(context).apply {
            text = title
            textSize = 16f
            setTextColor(0xFF000000.toInt())
            gravity = android.view.Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        linearLayout.addView(textView)

        val arrow = ImageView(context).apply {
            setImageResource(R.drawable.ic_github)
            layoutParams = LinearLayout.LayoutParams(context.dp(24f), context.dp(24f))
            setColorFilter(0xFF666666.toInt())
        }
        linearLayout.addView(arrow)

        card.addView(linearLayout)
        container.addView(card)

        return card
    }
}