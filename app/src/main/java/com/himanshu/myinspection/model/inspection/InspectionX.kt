package com.example.inspectionapplication.model.inspection

data class InspectionX(
    val inspectionId: Int = 0,
    val area: Area,
    val inspectionType: InspectionType,
    val survey: Survey
)