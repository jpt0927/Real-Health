package com.example.realhealth.util

import android.content.Context

object FavoriteManager {
    private const val PREF_NAME = "favorites"

    fun isFavorite(context: Context, placeId: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(placeId, false)
    }

    fun setFavorite(context: Context, placeId: String, isFavorite: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(placeId, isFavorite).apply()
    }
}