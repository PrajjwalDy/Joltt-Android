package com.hindu.cunow.Callback

import com.hindu.cunow.Model.InternshipModel

interface IInternshipCallback {
    fun onInternshipLoadFailed(str:String)
    fun onInternshipLoadSuccess(list:List<InternshipModel>)
}