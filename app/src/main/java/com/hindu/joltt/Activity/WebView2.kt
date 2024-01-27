package com.hindu.joltt.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hindu.cunow.R

class WebView2 : AppCompatActivity() {

    var url = ""
    var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view2)

        val intent = intent
        url =  intent.getStringExtra("url").toString()
        title = intent.getStringExtra("title").toString()

//        title_web2.text = title
//
//        webBack2.setOnClickListener {
//            /*webView2.clearCache(true)
//            webView2.clearHistory()
//            webView2.clearFormData()*/
//            finish()
//        }



        /*WebView.setWebContentsDebuggingEnabled(true)

        webView2.webViewClient = WebViewClient()
        val webSetting = webView2.settings
        webSetting.javaScriptEnabled = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webView2.loadUrl(url)*/

    }
//    override fun onBackPressed() {
//        if (webView2.canGoBack()) {
//            webView2.goBack()
//        } else {
//            super.onBackPressed()
//        }
//    }
}