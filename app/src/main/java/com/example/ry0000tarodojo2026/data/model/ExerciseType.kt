package com.example.ry0000tarodojo2026.data.model

import androidx.annotation.StringRes
import com.example.ry0000tarodojo2026.R

enum class ExerciseType(
    val id: String,
    @StringRes val displayNameRes: Int
) {
    NONE("none", R.string.exercise_none),
    SHAKE("shake", R.string.exercise_shake);

    companion object {
        /**
         * 保存されたIDからEnum定数を取得する
         */
        fun fromId(id: String?): ExerciseType {
            return entries.find { it.id == id } ?: NONE
        }
    }
}
