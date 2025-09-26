package com.example.icebox

import android.content.Context

object GeminiKeyStore {
    private const val PREFS_NAME = "gemini_api_prefs"
    private const val KEY_API = "gemini_api_key"

    fun getSavedKey(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_API, "")?.trim().orEmpty()
    }

    fun saveKey(context: Context, apiKey: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_API, apiKey.trim()).apply()
    }

    fun clearKey(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_API).apply()
    }
}