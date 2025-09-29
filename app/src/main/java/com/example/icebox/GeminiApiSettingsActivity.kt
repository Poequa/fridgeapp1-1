package com.example.icebox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.icebox.databinding.ActivityGeminiApiSettingsBinding
import com.google.android.material.snackbar.Snackbar

class GeminiApiSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeminiApiSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeminiApiSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupBottomNavigation(binding.bottomNav, R.id.nav_gemini_api)

        binding.openPortalButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.gemini_api_portal_url)))
            startActivity(intent)
        }

        val savedKey = GeminiKeyStore.getSavedKey(this)
        if (savedKey.isNotBlank()) {
            binding.apiKeyInput.setText(savedKey)
        }

        binding.saveButton.setOnClickListener {
            val key = binding.apiKeyInput.text?.toString()?.trim().orEmpty()
            if (key.isBlank()) {
                GeminiKeyStore.clearKey(this)
                showSnackbar(getString(R.string.gemini_api_settings_cleared))
            } else {
                GeminiKeyStore.saveKey(this, key)
                showSnackbar(getString(R.string.gemini_api_settings_saved))
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}