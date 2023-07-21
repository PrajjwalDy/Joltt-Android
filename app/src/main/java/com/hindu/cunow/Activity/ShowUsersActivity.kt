package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Adapter.UserAdapter
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R

class ShowUsersActivity : AppCompatActivity() {

    var id:String = ""
    var title:String = ""

    var userAdapter:UserAdapter? = null
    var userList: List<UserModel>? = null
    var idList:List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users)

        val intent = intent
        id = intent.getStringExtra("id").toString()
        title = intent.getStringExtra("title").toString()

        val recyclerView:RecyclerView = findViewById(R.id.showUser_RV)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userList = ArrayList()
        userAdapter = UserAdapter(this,userList as ArrayList<UserModel>)
        recyclerView.adapter = userAdapter

        idList = ArrayList()

       getPageFollowers()

    }

    private fun getFollowers(){
        val data = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(id).child("Followers")

        data.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (idList as ArrayList<String>).clear()
                for (snapshot in snapshot.children){
                    (idList as ArrayList<String>).add(snapshot.key!!)
                }
                showUser()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getFollowings(){
        val data = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(id).child("Following")

        data.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (idList as ArrayList<String>).clear()
                for (snapshot in snapshot.children){
                    (idList as ArrayList<String>).add(snapshot.key!!)
                }
                showUser()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getPageFollowers(){
        val data = FirebaseDatabase.getInstance().reference.child("Pages")
            .child(id).child("pageFollowers")

        data.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (idList as ArrayList<String>).clear()
                for (snapshot in snapshot.children){
                    (idList as ArrayList<String>).add(snapshot.key!!)
                }
                showUser()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showUser() {
        val userData = FirebaseDatabase.getInstance().reference.child("Users")
        userData.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (userList as ArrayList<UserModel>).clear()

                for (snapshot in snapshot.children){
                    val user = snapshot.getValue(UserModel::class.java)
                    for (id in idList!!){
                        if (user!!.uid == id){
                            (userList as ArrayList<UserModel>).add(user)
                        }
                    }
                }
                userAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}