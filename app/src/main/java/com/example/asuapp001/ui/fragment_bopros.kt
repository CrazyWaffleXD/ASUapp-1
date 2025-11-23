package com.example.asuapp001.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.asuapp001.R

class fragment_bopros : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bopros, container, false)

        val questionContainer = view.findViewById<View>(R.id.question_container)
        val question = view.findViewById<TextView>(R.id.question1)
        val answer = view.findViewById<TextView>(R.id.answer1)
        val arrow = view.findViewById<ImageView>(R.id.arrow1)

        // Устанавливаем высоту 0, если скрыто
        answer.apply {
            visibility = View.GONE
            layoutParams.height = 0
        }

        questionContainer.setOnClickListener {
            if (answer.visibility == View.GONE) {
                // Сначала делаем видимым, чтобы можно было измерить
                answer.visibility = View.VISIBLE

                // Принудительно измеряем высоту с текущим текстом
                answer.measure(
                    View.MeasureSpec.makeMeasureSpec(view.measuredWidth, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.UNSPECIFIED
                )
                val targetHeight = answer.measuredHeight

                // Анимируем от 0 до измеренной высоты
                val animator = ValueAnimator.ofInt(0, targetHeight)
                animator.addUpdateListener { animation ->
                    answer.layoutParams.height = animation.animatedValue as Int
                    answer.requestLayout()
                }
                animator.duration = 300
                animator.interpolator = DecelerateInterpolator()
                animator.start()

                // Повернуть стрелку вверх
                arrow.animate().rotation(180f).setDuration(300).start()
            } else {
                // Анимируем скрытие
                val currentHeight = answer.measuredHeight
                val animator = ValueAnimator.ofInt(currentHeight, 0)
                animator.addUpdateListener { animation ->
                    answer.layoutParams.height = animation.animatedValue as Int
                    answer.requestLayout()
                }
                animator.duration = 300
                animator.interpolator = DecelerateInterpolator()
                animator.start()

                // Через время скроем view
                animator.addListener(object : android.animation.Animator.AnimatorListener {
                    override fun onAnimationStart(animation: android.animation.Animator) {}
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        answer.visibility = View.GONE
                    }
                    override fun onAnimationCancel(animation: android.animation.Animator) {}
                    override fun onAnimationRepeat(animation: android.animation.Animator) {}
                })

                // Повернуть стрелку вниз
                arrow.animate().rotation(0f).setDuration(300).start()
            }
        }

        return view
    }
}