// Update your User.kt file
package com.example.currencyconverter1.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "password")
    val password: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    // Add new fields for profile
    @ColumnInfo(name = "phone")
    val phone: String = "",

    @ColumnInfo(name = "country")
    val country: String = "",

    @ColumnInfo(name = "profile_image")
    val profileImage: String = "",

    @ColumnInfo(name = "notifications_enabled")
    val notificationsEnabled: Boolean = true,

    @ColumnInfo(name = "dark_mode")
    val darkMode: Boolean = false,

    @ColumnInfo(name = "default_currency")
    val defaultCurrency: String = "USD"
)