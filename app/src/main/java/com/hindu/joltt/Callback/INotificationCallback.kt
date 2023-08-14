package com.hindu.joltt.Callback

import com.hindu.joltt.Model.NotificationModel

interface INotificationCallback {
    fun onNotificationCallbackLoadFailed(str:String)
    fun onNotificationCallbackLoadSuccess(list:List<NotificationModel>)
}