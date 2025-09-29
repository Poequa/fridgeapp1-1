package com.example.icebox

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun AppCompatActivity.setupBottomNavigation(
    bottomNav: BottomNavigationView,
    currentItemId: Int? = null
) {
    currentItemId?.let { bottomNav.selectedItemId = it }

    bottomNav.setOnItemSelectedListener { menuItem ->
        if (currentItemId != null && menuItem.itemId == currentItemId) {
            true
        } else {
            when (menuItem.itemId) {
                R.id.nav_add -> {
                    startActivity(Intent(this, AddIngredientActivity::class.java).apply {
                        addFlags(FLAG_ACTIVITY_NO_ANIMATION)
                    })
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_list -> {
                    startActivity(Intent(this, FridgeListActivity::class.java).apply {
                        addFlags(FLAG_ACTIVITY_NO_ANIMATION)
                    })
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        addFlags(FLAG_ACTIVITY_CLEAR_TOP)
                        addFlags(FLAG_ACTIVITY_NO_ANIMATION)
                    })
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_recipe -> {
                    startActivity(Intent(this, RecipeChatActivity::class.java).apply {
                        addFlags(FLAG_ACTIVITY_NO_ANIMATION)
                    })
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_gemini_api -> {
                    startActivity(Intent(this, GeminiApiSettingsActivity::class.java).apply {
                        addFlags(FLAG_ACTIVITY_NO_ANIMATION)
                    })
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    bottomNav.setOnItemReselectedListener { }
}