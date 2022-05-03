package com.hindu.cunow.Callback

import com.hindu.cunow.Model.CircleModel
import com.hindu.cunow.Model.UserModel

interface IChatListCallback {
    fun onChatListLoadFailed(str:String)
    fun onChatListLoadSuccess(list:List<UserModel>)
}