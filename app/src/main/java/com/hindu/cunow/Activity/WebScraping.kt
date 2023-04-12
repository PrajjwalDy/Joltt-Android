package com.hindu.cunow.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hindu.cunow.R
import org.jetbrains.anko.doAsync
import org.jsoup.Jsoup

class WebScraping : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_scraping)


        doAsync {



            var data = Jsoup.connect("https://devfolio.co/hackathons/open.").get()
            println(data)
            /*var details = data.getElementsByClass("main-box-inside")
            println(details)*/
        }
    }
}