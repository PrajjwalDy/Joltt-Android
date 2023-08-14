package com.hindu.joltt.Callback

import com.hindu.joltt.Model.EventModel

interface IEventCallback {
    fun onEventLoadFailed(str:String)
    fun onEventLoadSuccess(list:List<EventModel>)
}