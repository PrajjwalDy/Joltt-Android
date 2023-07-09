package com.hindu.cunow.Callback

import com.hindu.cunow.Model.ESModel

interface ISkillCallback {
    fun onSkillListLoadFailed(str: String)
    fun onSkillListLoadSuccess(list: List<ESModel>)

}