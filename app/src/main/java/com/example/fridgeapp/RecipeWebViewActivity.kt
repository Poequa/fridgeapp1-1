package com.example.fridgeapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class RecipeWebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // WebView를 레이아웃에서 사용하지 않고 직접 코드로 생성
        webView = WebView(this)
        setContentView(webView)

        // 설정 부분
        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.loadsImagesAutomatically = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false

        // 클라이언트 설정
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        // 인텐트에서 검색 키워드 받기
        val keyword = intent.getStringExtra("keyword") ?: "레시피"
        val searchUrl = "https://www.10000recipe.com/recipe/list.html?q=$keyword"

        // 여기서 실제 로딩 수행
        webView.loadUrl(searchUrl)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
