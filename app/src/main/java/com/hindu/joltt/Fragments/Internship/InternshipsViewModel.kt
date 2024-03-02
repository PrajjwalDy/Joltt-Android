package com.hindu.joltt.Fragments.Internship

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.joltt.Model.InternshipModel

class InternshipsViewModel : ViewModel(){

    private val allInternshipLiveData = MutableLiveData<List<InternshipModel>>()
    private val filterInternshipLiveData = MutableLiveData<List<InternshipModel>>()

    fun getAllInternshipsLiveData(): LiveData<List<InternshipModel>> = allInternshipLiveData
    fun getFilteredInternshipsLiveData(): LiveData<List<InternshipModel>> = filterInternshipLiveData


    init {
        loadData()
    }

     fun loadData(){
        val interList = ArrayList<InternshipModel>()
        val data = FirebaseDatabase.getInstance().reference.child("Internships")
        data.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                interList.clear()
                for(snapshot in snapshot.children){
                    val d1 = snapshot.getValue((InternshipModel::class.java))
                    interList.add(d1!!)
                }
                allInternshipLiveData.value = interList
            }

            override fun onCancelled(error: DatabaseError) {
               // internCallback.onInternshipLoadFailed(error.message)
            }

        })
    }


    fun filterInternships(selectedTheme:String){
        val allInternship = allInternshipLiveData.value ?: return
        val filterInternship = if (selectedTheme.isNotEmpty()) {
            allInternship.filter { it.iDuration == selectedTheme }
        } else {
            allInternship
        }
        filterInternshipLiveData.value = filterInternship
    }

}

