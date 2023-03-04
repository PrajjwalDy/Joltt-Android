package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hindu.cunow.R
import org.jetbrains.anko.doAsync
import org.jsoup.Jsoup

class WebScraping : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_scraping)


        doAsync {
            var data = Jsoup.connect("https://www.amazon.jobs/en/teams/internships-for-students").get()
            var details = data.getElementsByClass("job-tile")
            println("web scraping")
            for(img in details){
                var im = img.getElementsByClass("job-title").text()
                println(im)
            }

        }
    }
}