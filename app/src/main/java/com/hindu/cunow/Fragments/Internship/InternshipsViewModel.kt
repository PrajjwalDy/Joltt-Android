package com.hindu.cunow.Fragments.Internship

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.IInternshipCallback
import com.hindu.cunow.Model.InternshipModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InternshipsViewModel : ViewModel(), IInternshipCallback {

    private var internshipLiveData:MutableLiveData<List<InternshipModel>>? = null
    private val internCallback:IInternshipCallback = this
    private var messageError:MutableLiveData<String>? = null

    val internshipModel:MutableLiveData<List<InternshipModel>>?
        get() {
            if (internshipLiveData == null) {
                internshipLiveData = MutableLiveData()
                messageError = MutableLiveData()
                CoroutineScope(Dispatchers.IO).launch {
                    loadData()
                }
            }
            return internshipLiveData
        }
    private fun loadData(){
        val interList = ArrayList<InternshipModel>()
        val data = FirebaseDatabase.getInstance().reference.child("Internships")
        data.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                interList.clear()
                for(snapshot in snapshot.children){
                    val data = snapshot.getValue((InternshipModel::class.java))
                    interList.add(data!!)
                }
                internCallback.onInternshipLoadSuccess(interList)
            }

            override fun onCancelled(error: DatabaseError) {
                internCallback.onInternshipLoadFailed(error.message)
            }

        })
    }
    override fun onInternshipLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }
    override fun onInternshipLoadSuccess(list: List<InternshipModel>) {
        val mutableLiveData = internshipLiveData
        mutableLiveData!!.value = list
    }

}