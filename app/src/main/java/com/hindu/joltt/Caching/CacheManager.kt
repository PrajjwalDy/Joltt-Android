package com.hindu.joltt.Caching

import android.content.Context
import com.google.gson.Gson

object CacheManager {
    private const val PREF_NAME = "your_pref_name"

    // Save and retrieve like count
    fun saveLikeCount(context: Context, postId: String, likeCount: Int) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("likeCount_$postId", likeCount)
        editor.apply()
    }

    fun getLikeCount(context: Context, postId: String): Int {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt("likeCount_$postId", 0)
    }

    // Save and retrieve comment count
    fun saveCommentCount(context: Context, postId: String, commentCount: Int) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("commentCount_$postId", commentCount)
        editor.apply()
    }

    fun getCommentCount(context: Context, postId: String): Int {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt("commentCount_$postId", 0)
    }

    // Save and retrieve publisher details
    data class PublisherDetails(val name: String, val imageUrl: String)

    fun savePublisherDetails(context: Context, publisherId: String, details: PublisherDetails) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(details)
        editor.putString("publisherDetails_$publisherId", json)
        editor.apply()
    }

    fun getPublisherDetails(context: Context, publisherId: String): PublisherDetails? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("publisherDetails_$publisherId", null)
        return gson.fromJson(json, PublisherDetails::class.java)
    }
}