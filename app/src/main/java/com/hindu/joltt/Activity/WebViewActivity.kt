package com.hindu.joltt.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hindu.cunow.R

class WebViewActivity : AppCompatActivity() {

    var url = ""
    var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val intent = intent
        url =  intent.getStringExtra("url").toString()
        title = intent.getStringExtra("title").toString()





//        webView.webViewClient = WebViewClient()
//        val webSetting = webView.settings
//        webSetting.javaScriptEnabled = true
//
//        WebView.setWebContentsDebuggingEnabled(true)
//        webSetting.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//        webView.loadUrl(url)
    }
//    override fun onBackPressed() {
//        if (webView.canGoBack()) {
//            webView.goBack()
//        } else {
//            super.onBackPressed()
//        }
//    }
}