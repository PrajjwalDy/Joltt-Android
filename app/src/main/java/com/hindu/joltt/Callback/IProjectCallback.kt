package com.hindu.joltt.Callback

import com.hindu.joltt.Model.ProjectModel

interface IProjectCallback {
    fun onProjectLoadFailed(str:String)
    fun onProjectLoadSuccess(list:List<ProjectModel>)
}