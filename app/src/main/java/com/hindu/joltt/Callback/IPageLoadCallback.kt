package com.hindu.joltt.Callback
import com.hindu.joltt.Model.PageModel

interface IPageLoadCallback {
    fun onPageLoadFailed(str:String)
    fun onPageLoadSuccess(list:List<PageModel>)
}