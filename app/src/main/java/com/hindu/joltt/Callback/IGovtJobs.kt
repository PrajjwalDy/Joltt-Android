package com.hindu.joltt.Callback

import com.hindu.joltt.Model.EventModel
import com.hindu.joltt.Model.GjobModel

interface IGovtJobs {

    fun onEventLoadFailed(str:String)
    fun onEventLoadSuccess(list:List<GjobModel>)
}