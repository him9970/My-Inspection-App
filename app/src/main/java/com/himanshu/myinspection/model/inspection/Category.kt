package com.example.inspectionapplication.model.inspection

data class Category(
    val categoryId: Int = 0,
    val categoryName: String,
    val questions: List<Question>
)