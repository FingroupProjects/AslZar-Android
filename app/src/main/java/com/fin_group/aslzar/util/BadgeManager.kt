package com.fin_group.aslzar.util

import android.content.Context

class BadgeManager(context: Context) {
    private val prefs = context.getSharedPreferences("badge_prefs", Context.MODE_PRIVATE)

    fun saveBadgeCount(count: Int) {
        prefs.edit().putInt("badge_count", count).apply()
    }

    fun getBadgeCount(): Int {
        return prefs.getInt("badge_count", 0)
    }

}