package com.hindu.joltt.Activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hindu.cunow.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ChatGPT : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_gpt)

    }

    fun getResponse(query: String, callback: (String) -> Unit) {
        val url = "https://api.openai.com/v1/engines/davinci-002/completions"
        val apiKey = "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"

        val requestBody ="""
            {
              "prompt": "$query",
              "max_tokens": 500,
              "temperature": 0.3
            }
            """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error","API failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    Log.v("data",body)
                }
                else{
                    Log.v("data","empty")
                }
                val jsonObject = JSONObject(body!!)
                val jsonArray:JSONArray = jsonObject.getJSONArray("choices")
                val textResponse = jsonArray.getJSONObject(0).getString("text")
                callback(textResponse)
            }

        })


    }

}