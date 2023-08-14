package com.hindu.joltt.Callback

import com.hindu.joltt.Model.CourseModel

interface ICourseCallback {
    fun onCourseCallbackLoadFailed(str:String)
    fun onCourseCallbackLoadSuccess(list:List<CourseModel>)


}