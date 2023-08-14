package com.hindu.joltt.Callback

import com.hindu.joltt.Model.ESModel

interface IExperienceCallback {
    fun onExpListLoadFailed(str: String)
    fun onExpListLoadSuccess(list: List<ESModel>)
}