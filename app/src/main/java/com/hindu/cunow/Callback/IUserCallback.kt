package com.hindu.cunow.Callback

import com.hindu.cunow.Model.CircleModel
import com.hindu.cunow.Model.UserModel

interface IUserCallback {
    fun onUserLoadFailed(str:String)
    fun onUserLoadSuccess(list:List<UserModel>)
}