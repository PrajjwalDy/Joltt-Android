package com.hindu.joltt.Callback

import com.hindu.joltt.Model.AdmissionModel
import com.hindu.joltt.Model.EventModel

interface IAdmissionCallback {
    fun onEventLoadFailed(str:String)
    fun onEventLoadSuccess(list:List<AdmissionModel>)
}