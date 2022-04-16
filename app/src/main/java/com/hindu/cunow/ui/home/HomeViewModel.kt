package com.hindu.cunow.ui.home

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.IPostCallback
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.R

class HomeViewModel : ViewModel(), IPostCallback {

    private var postLiveData:MutableLiveData<List<PostModel>>? = null

    var followingList:MutableList<String>? = null
    private val postLoadCallback: IPostCallback = this
    private var messageError: MutableLiveData<String>? = null


    val postModel: MutableLiveData<List<PostModel>>?
    get() {
        if (postLiveData == null){
            postLiveData = MutableLiveData()
            messageError = MutableLiveData()
            checkFollowing()
        }
        val mutableLiveData = postLiveData
        return mutableLiveData

    }

    private fun loadPost() {
        //checkFollowing()
        val postList=ArrayList<PostModel>()
        val dataReference = FirebaseDatabase.getInstance().reference.child("Post")
        dataReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                for (snapshot in snapshot.children){
                    val postModel = snapshot.getValue<PostModel>(PostModel::class.java) as PostModel
                    for (id in (followingList as ArrayList<String>)){
                        if (postModel.publisher == id){
                            postList.add(postModel)
                       }
                    }
                }
                postLoadCallback.onPostPCallbackLoadSuccess(postList)
            }

            override fun onCancelled(error: DatabaseError) {
                postLoadCallback.onPostCallbackLoadFailed(error.message)
            }

        })
    }

    private fun checkFollowing(){
        followingList = ArrayList()

        val userDataRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")
        userDataRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (followingList as ArrayList<String>).clear()

                    for (snapshot in snapshot.children){
                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                    loadPost()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



    override fun onPostCallbackLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onPostPCallbackLoadSuccess(list: List<PostModel>) {
        val mutableLiveData = postLiveData
        mutableLiveData!!.value = list
    }


}