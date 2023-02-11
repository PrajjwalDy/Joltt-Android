package com.hindu.cunow.Fragments.Projects.AllProjects

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.IProjectCallback
import com.hindu.cunow.Model.ProjectModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllProjectsViewModel : ViewModel(), IProjectCallback {
    private var projectLiveData: MutableLiveData<List<ProjectModel>>? = null
    private val projectCallback: IProjectCallback = this
    private var messageError: MutableLiveData<String>? = null

    val projectModel: MutableLiveData<List<ProjectModel>>?
        get() {
            if (projectLiveData == null) {
                projectLiveData = MutableLiveData()
                messageError = MutableLiveData()
                CoroutineScope(Dispatchers.IO).launch {
                    loadData()
                }

            }
            return projectLiveData
        }

    private fun loadData(){
        val eventList = ArrayList<ProjectModel>()
        val data = FirebaseDatabase.getInstance().reference.child("Projects")
        data.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                for(snapshot in snapshot.children){
                    val data = snapshot.getValue((ProjectModel::class.java))
                    eventList.add(data!!)
                }
                projectCallback.onProjectLoadSuccess(eventList)
            }

            override fun onCancelled(error: DatabaseError) {
                projectCallback.onProjectLoadFailed(error.message)
            }

        })
    }

    override fun onProjectLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onProjectLoadSuccess(list: List<ProjectModel>) {
        val mutableLiveData = projectLiveData
        mutableLiveData!!.value = list
    }

}