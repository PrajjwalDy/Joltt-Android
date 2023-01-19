package com.hindu.cunow.Fragments.HashTag

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.IHashTagCallBack
import com.hindu.cunow.Callback.IUserCallback
import com.hindu.cunow.Model.HashTagModel
import com.hindu.cunow.Model.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HasTagViewModel : ViewModel(),IHashTagCallBack{

    private var hashLiveData: MutableLiveData<List<HashTagModel>>? = null
    private val hashCallback: IHashTagCallBack = this
    private var messageError: MutableLiveData<String>? =null

    val hashTagModel:MutableLiveData<List<HashTagModel>>?
    get() {
        if (hashLiveData == null){
            hashLiveData = MutableLiveData()
            messageError = MutableLiveData()
            CoroutineScope(Dispatchers.IO).launch {
                loadTags()
            }
        }
        val mutableLiveData = hashLiveData
        return mutableLiveData
    }

    private suspend fun loadTags() {
        val tagList = ArrayList<HashTagModel>()
        val userData = FirebaseDatabase.getInstance().reference.child("hashtags")
        userData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tagList.clear()
                for (snapshot in snapshot.children){
                    val user = snapshot.getValue(HashTagModel::class.java)
                    tagList.add(user!!)
                }
                hashCallback.onTagListLoadSuccess(tagList)
            }

            override fun onCancelled(error: DatabaseError) {
                hashCallback.onTagListLoadFailed(error.message)
            }

        })
    }

    override fun onTagListLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onTagListLoadSuccess(list: List<HashTagModel>) {
        val mutableLiveData = hashLiveData
        mutableLiveData!!.value = list
    }

}