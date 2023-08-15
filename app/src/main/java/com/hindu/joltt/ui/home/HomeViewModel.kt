package com.hindu.joltt.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.joltt.Callback.IPostCallback
import com.hindu.joltt.Model.PostModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel() : ViewModel(), IPostCallback {

    private var postLiveData: MutableLiveData<List<PostModel>>? = null
    var followingList: MutableList<String>? = null
    var pageList: MutableList<String>? = null
    var userInterest: MutableList<String>? = mutableListOf()
    var tagList = mutableListOf<String>()
    private val postLoadCallback: IPostCallback = this
    private var messageError: MutableLiveData<String>? = null

    val postModel: MutableLiveData<List<PostModel>>?
        get() {
            if (postLiveData == null) {
                postLiveData = MutableLiveData()
                messageError = MutableLiveData()

                viewModelScope.launch(Dispatchers.IO) {
                    loadUserInterestFromFirebase()
                    pageList()
                    checkFollowing()
                }
            }
            return postLiveData

        }

    private fun loadPost2(){
        val postSet = HashSet<PostModel>()
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Post")
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                postSet.clear()
                for (snapshot in snapshot.children){
                    val postModel = snapshot.getValue(PostModel::class.java) as PostModel
                    if ((followingList as ArrayList<String>).contains(postModel.publisher)||(pageList as ArrayList<String>).contains(postModel.publisher)|| postModelUserInterest(postModel)){
                        postSet.add(postModel)
                    }
                }
                val postList = postSet.toList()
                postLoadCallback.onPostPCallbackLoadSuccess(postList)
            }

            override fun onCancelled(error: DatabaseError) {
                postLoadCallback.onPostCallbackLoadFailed(error.message)
            }

        })
    }
    private fun postModelUserInterest(postModel: PostModel):Boolean{
        val userInterestString = userInterest!!.joinToString("#")
        val caption = postModel.caption!!.trim(){it <=' '}
        val feedTags = caption.split(" ")

        for (tag in feedTags){
            if (tag.startsWith("#") && userInterestString.contains(tag)){
                return true
            }
        }
        return false
    }
    //CHECK FOLLOWING
    private suspend fun checkFollowing() {
        followingList = ArrayList()

        val userDataRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")
        userDataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    (followingList as ArrayList<String>).clear()

                    for (snapshot in snapshot.children) {
                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        //loadPost()
                        loadPost2()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        userDataRef.keepSynced(true)
    }
    private suspend fun pageList() {
        pageList = ArrayList()
        val data = FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("FollowingPages")

        data.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    (pageList as ArrayList<String>).clear()
                    for (snapshot in snapshot.children) {
                        snapshot.key?.let { (pageList as ArrayList<String>).add(it) }
                        //loadPagePost()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    //New Filtration Function
    private fun loadUserInterestFromFirebase() {
        val userInterestRef = FirebaseDatabase
            .getInstance()
            .getReference("UserInterest")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("interest")
        userInterestRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userInterest!!.clear()
                for (interestSnapshot in dataSnapshot.children) {
                    val interest = interestSnapshot.getValue(String::class.java)
                    if (interest != null) {
                        userInterest!!.add(interest)
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    loadPost2()
                    //checkFollowingList()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO: Handle error
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