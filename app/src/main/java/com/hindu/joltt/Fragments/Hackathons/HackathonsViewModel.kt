package com.hindu.joltt.Fragments.Hackathons

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.joltt.Callback.IHackathonCallback
import com.hindu.joltt.Model.HackathonModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HackathonsViewModel : ViewModel() {
//    private var hackathonLiveData: MutableLiveData<List<HackathonModel>>? = null
//    private val hackCallback: IHackathonCallback = this
//    private var messageError:MutableLiveData<String>? = null

    private val allHackathonsLiveData = MutableLiveData<List<HackathonModel>>()
    private val filteredHackathonsLiveData = MutableLiveData<List<HackathonModel>>()

    fun getAllHackathonsLiveData(): LiveData<List<HackathonModel>> = allHackathonsLiveData
    fun getFilteredHackathonsLiveData(): LiveData<List<HackathonModel>> = filteredHackathonsLiveData


    init {
        loadData()
    }


    fun loadData() {
        val hackList = ArrayList<HackathonModel>()
        val data = FirebaseDatabase.getInstance().reference.child("Hackathons")
        data.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                hackList.clear()
                for (snapshot in snapshot.children) {
                    val d1 = snapshot.getValue(HackathonModel::class.java)
                    hackList.add(d1!!)
                }
                allHackathonsLiveData.value = hackList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    fun filterHackathons(selectedTheme: String) {
        val allHackathons = allHackathonsLiveData.value ?: return
        val filteredHackathons = if (selectedTheme.isNotEmpty()) {
            allHackathons.filter { it.hTheme == selectedTheme }
        } else {
            allHackathons
        }
        filteredHackathonsLiveData.value = filteredHackathons
    }

}