package com.example.inspectionapplication.model.inspection

data class AnswerChoice(
    val answerChoicesId: Int = 0,
    val answerChoicesName: String,
    val answerChoicesScore: Double,
    var isSelected: Boolean = false
)