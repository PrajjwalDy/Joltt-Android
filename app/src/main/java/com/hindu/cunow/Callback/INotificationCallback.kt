package com.hindu.cunow.Callback

import com.hindu.cunow.Model.NotificationModel

interface INotificationCallback {
    fun onNotificationCallbackLoadFailed(str:String)
    fun onNotificationCallbackLoadSuccess(list:List<NotificationModel>)
}