package com.hindu.joltt.Callback

import com.hindu.joltt.Model.JoinedCircleModel

interface IJoinedCircleCallback {
    fun onJoinedCircleLoadFailed(str:String)
    fun onJoinedCircleLoadSuccess(list:List<JoinedCircleModel>)
}