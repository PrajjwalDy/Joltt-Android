package com.hindu.cunow.Callback

import com.hindu.cunow.Model.PostModel

interface IPostCallback {

    fun onPostCallbackLoadFailed(str:String)
    fun onPostPCallbackLoadSuccess(list:List<PostModel>)
}