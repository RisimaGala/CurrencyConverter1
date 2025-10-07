package com.example.currencyconverter1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter1.data.CurrencyDatabase
import com.example.currencyconverter1.data.entity.User
import com.example.currencyconverter1.repository.UserRepository
import com.example.currencyconverter1.utils.PrefManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val database: CurrencyDatabase,
    private val prefManager: PrefManager
) : ViewModel() {
    private val repository = UserRepository(database)

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    // Add these to UserViewModel.kt
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _settingsState = MutableStateFlow<SettingsState>(SettingsState.Idle)
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val user = repository.loginUser(email, password)
                if (user != null) {
                    prefManager.saveUser(user.id, user.email, user.name)
                    _loginState.value = LoginState.Success(user)
                } else {
                    _loginState.value = LoginState.Error("Invalid email or password")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                val user = User(email = email, password = password, name = name)
                val userId = repository.registerUser(user)
                if (userId != -1L) {
                    prefManager.saveUser(userId, email, name)
                    _registerState.value = RegisterState.Success(user.copy(id = userId))
                } else {
                    _registerState.value = RegisterState.Error("Email already exists")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Registration failed: ${e.message}")
            }
        }
    }

    // Add these methods
    fun updateProfile(userId: Long, name: String, phone: String, country: String) {
        _profileState.value = ProfileState.Loading
        viewModelScope.launch {
            try {
                val currentUser = repository.getUserById(userId)
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        name = name,
                        phone = phone,
                        country = country
                    )
                    val success = repository.updateUser(updatedUser)
                    if (success) {
                        prefManager.saveUser(userId, currentUser.email, name, phone, country)
                        _profileState.value = ProfileState.Success(updatedUser)
                    } else {
                        _profileState.value = ProfileState.Error("Failed to update profile")
                    }
                } else {
                    _profileState.value = ProfileState.Error("User not found")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error("Update failed: ${e.message}")
            }
        }
    }

    fun updateSettings(notificationsEnabled: Boolean, darkMode: Boolean, defaultCurrency: String) {
        _settingsState.value = SettingsState.Loading
        viewModelScope.launch {
            try {
                prefManager.saveSettings(notificationsEnabled, darkMode, defaultCurrency)
                _settingsState.value = SettingsState.Success
            } catch (e: Exception) {
                _settingsState.value = SettingsState.Error("Failed to save settings: ${e.message}")
            }
        }
    }

    fun loadUserProfile(userId: Long) {
        viewModelScope.launch {
            try {
                val user = repository.getUserById(userId)
                if (user != null) {
                    _profileState.value = ProfileState.Success(user)
                } else {
                    _profileState.value = ProfileState.Error("User not found")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error("Failed to load profile: ${e.message}")
            }
        }
    }

    fun isUserLoggedIn(): Boolean = prefManager.isUserLoggedIn()
    fun getCurrentUser() = prefManager.getCurrentUser()
    fun logout() = prefManager.clearUser()

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        data class Success(val user: User) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }

    // Add new state classes
    sealed class ProfileState {
        object Idle : ProfileState()
        object Loading : ProfileState()
        data class Success(val user: User) : ProfileState()
        data class Error(val message: String) : ProfileState()
    }

    sealed class SettingsState {
        object Idle : SettingsState()
        object Loading : SettingsState()
        object Success : SettingsState()
        data class Error(val message: String) : SettingsState()
    }
}