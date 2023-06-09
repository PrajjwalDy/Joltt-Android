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

            val doc = Jsoup.connect("https://devfolio.co/hackathons/open").get()

            val elements = doc.select("div [class=sc-iBkjds.eTjKde.CompactHackathonCard__StyledCard-sc-1294b781-0.bBknpa]")

            println(elements)
            /*for (element in elements){
                val data= element.text()
                println(data)
            }*/

        }
    }
}