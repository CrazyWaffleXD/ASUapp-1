package com.example.asuapp001.utils.Question.ChangeDp

import android.content.Context
import android.util.TypedValue

fun Context.dp(value: Float): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value,
        resources.displayMetrics
    ).toInt()