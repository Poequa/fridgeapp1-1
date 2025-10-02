package com.example.icebox

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_nav)
        setupBottomNavigation(bottomNav, R.id.nav_home)

        val tipTitle = findViewById<TextView>(R.id.text_tip_title)
        val tipBody = findViewById<TextView>(R.id.text_tip_body)

        val hasGeminiKey = GeminiKeyStore.getSavedKey(this).isNotEmpty()
        if (hasGeminiKey) {
            tipTitle.text = getString(R.string.gemini_tip_title_connected)
            tipBody.text = getString(R.string.gemini_tip_body_connected)
        } else {
            tipTitle.text = getString(R.string.gemini_tip_title_need_setup)
            tipBody.text = getString(R.string.gemini_tip_body_need_setup)
        }
    }
}