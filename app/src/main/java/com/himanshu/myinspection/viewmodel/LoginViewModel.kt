package com.example.inspectionapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inspectionapplication.model.roomdatabase.User
import com.himanshu.myinspection.model.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun insertUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUser(user)
        }
    }

    suspend fun getUser(username: String, password: String): User? {
        return repository.getUser(username, password)
    }

}