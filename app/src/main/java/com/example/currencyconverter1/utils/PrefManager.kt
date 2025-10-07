// Update PrefManager.kt - add these constants and methods
companion object {
    // ... existing constants ...

    // Add new constants for settings
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_COUNTRY = "user_country"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_DEFAULT_CURRENCY = "default_currency"
}

// Update saveUser method
fun saveUser(userId: Long, email: String, name: String, phone: String = "", country: String = "") {
    with(sharedPreferences.edit()) {
        putLong(KEY_USER_ID, userId)
        putString(KEY_USER_EMAIL, email)
        putString(KEY_USER_NAME, name)
        putString(KEY_USER_PHONE, phone)
        putString(KEY_USER_COUNTRY, country)
        putBoolean(KEY_IS_LOGGED_IN, true)
        apply()
    }
}

// Add new methods for settings
fun saveSettings(notificationsEnabled: Boolean, darkMode: Boolean, defaultCurrency: String) {
    with(sharedPreferences.edit()) {
        putBoolean(KEY_NOTIFICATIONS_ENABLED, notificationsEnabled)
        putBoolean(KEY_DARK_MODE, darkMode)
        putString(KEY_DEFAULT_CURRENCY, defaultCurrency)
        apply()
    }
}

fun getNotificationsEnabled(): Boolean {
    return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
}

fun getDarkMode(): Boolean {
    return sharedPreferences.getBoolean(KEY_DARK_MODE, false)
}

fun getDefaultCurrency(): String {
    return sharedPreferences.getString(KEY_DEFAULT_CURRENCY, "USD") ?: "USD"
}

// Update getCurrentUser to include new fields
fun getCurrentUser(): User {
    return User(
        id = getCurrentUserId(),
        email = sharedPreferences.getString(KEY_USER_EMAIL, "") ?: "",
        name = sharedPreferences.getString(KEY_USER_NAME, "") ?: "",
        password = "",
        phone = sharedPreferences.getString(KEY_USER_PHONE, "") ?: "",
        country = sharedPreferences.getString(KEY_USER_COUNTRY, "") ?: "",
        notificationsEnabled = getNotificationsEnabled(),
        darkMode = getDarkMode(),
        defaultCurrency = getDefaultCurrency()
    )
}