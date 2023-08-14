package com.hindu.joltt.Activity

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_web_view2.title_web2
import kotlinx.android.synthetic.main.activity_web_view2.webBack2
import kotlinx.android.synthetic.main.activity_web_view2.webView2

class WebView2 : AppCompatActivity() {

    var url = ""
    var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view2)

        val intent = intent
        url =  intent.getStringExtra("url").toString()
        title = intent.getStringExtra("title").toString()

        title_web2.text = title

        webBack2.setOnClickListener {
            /*webView2.clearCache(true)
            webView2.clearHistory()
            webView2.clearFormData()*/
            finish()
        }



        /*WebView.setWebContentsDebuggingEnabled(true)

        webView2.webViewClient = WebViewClient()
        val webSetting = webView2.settings
        webSetting.javaScriptEnabled = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webView2.loadUrl(url)*/

    }
    override fun onBackPressed() {
        if (webView2.canGoBack()) {
            webView2.goBack()
        } else {
            super.onBackPressed()
        }
    }
}