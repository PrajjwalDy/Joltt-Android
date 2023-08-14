package com.hindu.joltt.Callback

import com.hindu.joltt.Model.UserModel

interface IUserCallback {
    fun onUserLoadFailed(str:String)
    fun onUserLoadSuccess(list:List<UserModel>)
}