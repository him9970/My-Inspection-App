package com.himanshu.myinspection.model.repository

import com.example.inspectionapplication.model.roomdatabase.User
import com.example.inspectionapplication.model.roomdatabase.UserDao

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUser(username: String, password: String): User? {
        return userDao.getUser(username, password)
    }

}