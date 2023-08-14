package com.hindu.joltt.Callback

import com.hindu.joltt.Model.InternshipModel

interface IInternshipCallback {
    fun onInternshipLoadFailed(str:String)
    fun onInternshipLoadSuccess(list:List<InternshipModel>)
}