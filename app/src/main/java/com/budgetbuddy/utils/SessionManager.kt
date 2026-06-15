package com.budgetbuddy.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("budget_buddy_prefs", Context.MODE_PRIVATE)

    fun saveLoginSession(userId: Int) {
        prefs.edit().putInt("user_id", userId).putBoolean("is_logged_in", true).apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun getUserId(): Int = prefs.getInt("user_id", -1)

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    // Store Double as String
    fun setMonthlyMinGoal(userId: Int, min: Double) {
        prefs.edit().putString("min_goal_$userId", min.toString()).apply()
    }

    fun setMonthlyMaxGoal(userId: Int, max: Double) {
        prefs.edit().putString("max_goal_$userId", max.toString()).apply()
    }

    // Retrieve Double from String
    fun getMonthlyMinGoal(userId: Int): Double {
        val str = prefs.getString("min_goal_$userId", "0.0") ?: "0.0"
        return str.toDoubleOrNull() ?: 0.0
    }

    fun getMonthlyMaxGoal(userId: Int): Double {
        val str = prefs.getString("max_goal_$userId", "0.0") ?: "0.0"
        return str.toDoubleOrNull() ?: 0.0
    }
}