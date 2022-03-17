package com.hindu.cunow.Callback

import com.hindu.cunow.Model.JoinedCircleModel
import com.hindu.cunow.Model.VibesModel

interface IJoinedCircleCallback {
    fun onJoinedCircleLoadFailed(str:String)
    fun onJoinedCircleLoadSuccess(list:List<JoinedCircleModel>)
}