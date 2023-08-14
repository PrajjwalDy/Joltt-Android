package com.hindu.joltt.Callback

import com.hindu.joltt.Model.UserModel

interface IChatListCallback {
    fun onChatListLoadFailed(str:String)
    fun onChatListLoadSuccess(list:List<UserModel>)
}