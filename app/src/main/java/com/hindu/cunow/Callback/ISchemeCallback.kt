package com.hindu.cunow.Callback

import com.hindu.cunow.Model.SchemeModel

interface ISchemeCallback {
    fun onSchemeLoadFailed(str:String)
    fun onSchemeLoadSuccess(list:List<SchemeModel>)
}