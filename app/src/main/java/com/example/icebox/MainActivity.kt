package com.example.icebox

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_add -> startActivity(Intent(this, AddIngredientActivity::class.java))
                R.id.nav_list -> startActivity(Intent(this, FridgeListActivity::class.java))
                R.id.nav_recipe -> startActivity(Intent(this, RecipeChatActivity::class.java))
                R.id.nav_gemini_api -> startActivity(Intent(this, GeminiApiSettingsActivity::class.java))
            }
            true
        }
    }
}