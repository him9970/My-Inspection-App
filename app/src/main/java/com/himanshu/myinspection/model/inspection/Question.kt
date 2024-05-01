package com.example.inspectionapplication.model.inspection

data class Question(
    val questionId: Int = 0,
    val answerChoices: List<AnswerChoice>,
    val questionName: String,
    var selectedAnswerChoiceId: Int = 0
)