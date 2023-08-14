package com.hindu.joltt.Callback

import com.hindu.joltt.Model.SchemeModel

interface ISchemeCallback {
    fun onSchemeLoadFailed(str:String)
    fun onSchemeLoadSuccess(list:List<SchemeModel>)
}