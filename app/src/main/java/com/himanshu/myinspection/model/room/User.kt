package com.example.inspectionapplication.model.roomdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val username: String,
    val password: String
)
