package com.fin_group.aslzar.util

import android.content.Context

class BadgeManager(context: Context, prefsName: String) {
    private val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    fun saveBadgeCount(count: Int) {
        prefs.edit().putInt("badge_count", count).apply()
    }

    fun getBadgeCount(): Int {
        return prefs.getInt("badge_count", 0)
    }

    fun clearBadge() {
        prefs.edit().remove("badge_count").apply()
    }
}
