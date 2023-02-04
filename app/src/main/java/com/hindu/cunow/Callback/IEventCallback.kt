package com.hindu.cunow.Callback

import com.hindu.cunow.Model.EventModel

interface IEventCallback {
    fun onEventLoadFailed(str:String)
    fun onEventLoadSuccess(list:List<EventModel>)
}