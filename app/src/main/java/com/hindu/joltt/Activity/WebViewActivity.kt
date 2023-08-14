package com.hindu.joltt.Activity

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.browser.customtabs.CustomTabsIntent
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_web_view.title_web
import kotlinx.android.synthetic.main.activity_web_view.webBack
import kotlinx.android.synthetic.main.activity_web_view.webView

class WebViewActivity : AppCompatActivity() {

    var url = ""
    var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val intent = intent
        url =  intent.getStringExtra("url").toString()
        title = intent.getStringExtra("title").toString()

        title_web.text = title

        webBack.setOnClickListener {
            webView.clearCache(true)
            webView.clearHistory()
            webView.clearFormData()
            finish()
        }



        webView.webViewClient = WebViewClient()
        val webSetting = webView.settings
        webSetting.javaScriptEnabled = true

        WebView.setWebContentsDebuggingEnabled(true)
        webSetting.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.loadUrl(url)
    }
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}