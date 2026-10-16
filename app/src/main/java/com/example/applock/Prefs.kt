package com.example.applock

import android.content.Context

object Prefs {
    private fun sp(c: Context) = c.getSharedPreferences("applock", Context.MODE_PRIVATE)
    fun locked(c: Context): Set<String> = sp(c).getStringSet("locked", emptySet()) ?: emptySet()
    fun setLocked(c: Context, s: Set<String>) = sp(c).edit().putStringSet("locked", HashSet(s)).apply()
}
