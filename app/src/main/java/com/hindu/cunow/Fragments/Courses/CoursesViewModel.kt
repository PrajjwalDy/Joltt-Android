package com.hindu.cunow.Fragments.Courses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.ICourseCallback
import com.hindu.cunow.Model.CourseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoursesViewModel : ViewModel(), ICourseCallback {
    private var courseLiveData: MutableLiveData<List<CourseModel>>? = null
    private val courseCallback: ICourseCallback = this
    private var messageError: MutableLiveData<String>? = null

    val courseModel: MutableLiveData<List<CourseModel>>?
        get() {
            if (courseLiveData == null) {
                courseLiveData = MutableLiveData()
                messageError = MutableLiveData()
                CoroutineScope(Dispatchers.IO).launch {
                    loadData()
                }
            }
            return courseLiveData
        }

    private fun loadData(){
        val eventList = ArrayList<CourseModel>()
        val data = FirebaseDatabase.getInstance().reference.child("Courses")
        data.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                for(snapshot in snapshot.children){
                    val data = snapshot.getValue((CourseModel::class.java))
                    eventList.add(data!!)
                }
                courseCallback.onCourseCallbackLoadSuccess(eventList)
            }

            override fun onCancelled(error: DatabaseError) {
                courseCallback.onCourseCallbackLoadFailed(error.message)
            }

        })
    }

    override fun onCourseCallbackLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onCourseCallbackLoadSuccess(list: List<CourseModel>) {
        val mutableLiveData = courseLiveData
        mutableLiveData!!.value = list
    }


}