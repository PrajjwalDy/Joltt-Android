package com.hindu.joltt.Callback

import com.hindu.joltt.Model.PostModel

interface IPostCallback {

    fun onPostCallbackLoadFailed(str:String)
    fun onPostPCallbackLoadSuccess(list:List<PostModel>)
}