package com.example.icebox

import android.content.Context

object GeminiKeyStore {
    private const val PREFS_NAME = "gemini_prefs"
    private const val PREF_KEY_API = "gemini_api_key"

    fun getApiKey(context: Context): String {
        val buildConfigKey = BuildConfig.GEMINI_API_KEY
        if (buildConfigKey.isNotBlank()) {
            return buildConfigKey
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(PREF_KEY_API, "").orEmpty()
    }

    fun saveApiKey(context: Context, apiKey: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(PREF_KEY_API, apiKey)
            .apply()
    }
}