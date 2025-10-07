package com.example.currencyconverter1.repository

import com.example.currencyconverter1.data.CurrencyDatabase
import com.example.currencyconverter1.data.entity.User

class UserRepository(private val database: CurrencyDatabase) {
    private val userDao = database.userDao()

    suspend fun registerUser(user: User): Long {
        val existingUser = userDao.getUserByEmail(user.email)
        return if (existingUser == null) {
            userDao.insertUser(user)
        } else {
            -1L // User already exists
        }
    }

    suspend fun loginUser(email: String, password: String): User? {
        return userDao.getUser(email, password)
    }

    suspend fun checkEmailExists(email: String): Boolean {
        return userDao.checkEmailExists(email) > 0
    }

    // Add these methods to UserRepository.kt
    suspend fun updateUser(user: User): Boolean {
        return try {
            userDao.updateUser(user)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserById(userId: Long): User? {
        return try {
            userDao.getUserById(userId)
        } catch (e: Exception) {
            null
        }
    }
}