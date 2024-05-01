package com.example.inspectionapplication.api

import com.example.inspectionapplication.model.inspection.Inspection
import com.example.inspectionapplication.model.inspection.Question
import com.example.inspectionapplication.model.login.LoginRequest
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @POST("/api/register")
    suspend fun login(@Body loginRequest: LoginRequest): Response<Unit>

    @POST("/api/inspections/submit")
    suspend fun submitInspection(@Body inspection: Inspection): Response<Unit>

    @GET("/api/questions")
    fun fetchQuestions(): Deferred<List<Question>>
}