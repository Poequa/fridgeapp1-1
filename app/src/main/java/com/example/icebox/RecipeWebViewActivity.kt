package com.example.icebox

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class RecipeWebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)

        webView.loadUrl("https://www.10000recipe.com/")
    }
}
