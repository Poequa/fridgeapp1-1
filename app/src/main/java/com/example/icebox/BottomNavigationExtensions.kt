package com.example.icebox

import android.content.Intent
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
                    startActivity(Intent(this, AddIngredientActivity::class.java))
                    true
                }
                R.id.nav_list -> {
                    startActivity(Intent(this, FridgeListActivity::class.java))
                    true
                }
                R.id.nav_recipe -> {
                    startActivity(Intent(this, RecipeChatActivity::class.java))
                    true
                }
                R.id.nav_gemini_api -> {
                    startActivity(Intent(this, GeminiApiSettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    bottomNav.setOnItemReselectedListener { }
}