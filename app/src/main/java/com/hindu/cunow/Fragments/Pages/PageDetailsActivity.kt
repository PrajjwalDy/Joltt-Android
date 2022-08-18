package com.hindu.cunow.Fragments.Pages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.CreatePagePostActivity
import com.hindu.cunow.Adapter.PostAdapter
import com.hindu.cunow.Model.PageModel
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_create_page_post.*
import kotlinx.android.synthetic.main.activity_page_details.*
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.page_post_dialog.view.*

class PageDetailsActivity : AppCompatActivity() {
    private var pageId = ""
    private var pageAdmin = ""
    private var pageName = ""
    var postList:MutableList<PostModel>? = null
    private var postAdapter:PostAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_details)

        val intent = intent
        pageAdmin = intent.getStringExtra("pageAdmin").toString()
        pageName = intent.getStringExtra("pageName").toString()
        pageId = intent.getStringExtra("pageId").toString()
        checkFollowing()

        if (pageAdmin == FirebaseAuth.getInstance().currentUser!!.uid){
            pageFollow_Btn_pd.visibility = View.GONE
            createPost_page.visibility = View.VISIBLE
            createPost_page_img.visibility = View.VISIBLE
            pageNotification.visibility = View.VISIBLE
        }else{
            pageFollow_Btn_pd.visibility = View.VISIBLE
            createPost_page.visibility = View.GONE
            createPost_page_img.visibility = View.GONE
            pageNotification.visibility = View.GONE
        }
        pageFollow_Btn_pd.setOnClickListener {
            when (pageFollow_Btn_pd.text.toString()) {
                "Follow" -> {
                    FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("FollowingPages")
                        .child(pageId)
                        .setValue(true)

                    FirebaseDatabase.getInstance().reference.child("Pages")
                        .child(pageId)
                        .child("pageFollowers")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(true)
                }
                "Unfollow"->{
                    FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("FollowingPages")
                        .child(pageId)
                        .removeValue()

                    FirebaseDatabase.getInstance().reference.child("Pages")
                        .child(pageId)
                        .child("pageFollowers")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .removeValue()
                }

            }
        }

        createPost_page_img.setOnClickListener {

            val dialogView = LayoutInflater.from(this).inflate(R.layout.page_post_dialog,null)

            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)

            val alertdialog = dialogBuilder.show()

            dialogView.selectImagePage.setOnClickListener {
                val intent = Intent(this,CreatePagePostActivity::class.java)
                intent.putExtra("Id",pageId)
                intent.putExtra("admin",pageAdmin)
                intent.putExtra("name",pageName_detailsPage_appbar.text.toString())
                startActivity(intent)
                alertdialog.dismiss()
            }
            dialogView.selectVideoPage.setOnClickListener {
                val intent = Intent(this,CreateVideoPostPage::class.java)
                intent.putExtra("Id",pageId)
                intent.putExtra("admin",pageAdmin)
                intent.putExtra("name",pageName_detailsPage_appbar.text.toString())
                startActivity(intent)
                alertdialog.dismiss()
            }
        }

        createPost_page.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.page_post_dialog,null)

            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)

            val alertdialog = dialogBuilder.show()

            dialogView.selectImagePage.setOnClickListener {
                val intent = Intent(this,CreatePagePostActivity::class.java)
                intent.putExtra("Id",pageId)
                intent.putExtra("admin",pageAdmin)
                intent.putExtra("name",pageName_detailsPage_appbar.text.toString())
                startActivity(intent)
                alertdialog.dismiss()
            }
            dialogView.selectVideoPage.setOnClickListener {
                val intent = Intent(this,CreateVideoPostPage::class.java)
                intent.putExtra("Id",pageId)
                intent.putExtra("admin",pageAdmin)
                intent.putExtra("name",pageName_detailsPage_appbar.text.toString())
                startActivity(intent)
                alertdialog.dismiss()
            }
        }

        loadPagesDetails()
        val recyclerView:RecyclerView = findViewById(R.id.pagePost_RV)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout =true
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.stackFromEnd = true

        postList = ArrayList()
        postAdapter = PostAdapter(this,postList as ArrayList<PostModel>)
        recyclerView.adapter = postAdapter
        loadPagePosts()
        totalFollowers()

    }

    private fun loadPagesDetails(){
        val data = FirebaseDatabase.getInstance().reference
            .child("Pages")
            .child(pageId)
        data.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val pageData = snapshot.getValue(PageModel::class.java)
                    pageName_detailsPage_appbar.text = pageData!!.pageName
                    pageDescription_pd.text = pageData.pageDescription
                    //pageFollowersCount.text = pageData.
                    Glide.with(this@PageDetailsActivity).load(pageData.pageIcon).into(pageIcon_pd)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun loadPagePosts(){
        val database = FirebaseDatabase.getInstance().reference
            .child("Post")
        database.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (postList as ArrayList<PostModel>).clear()
                    for (snapshot in snapshot.children){
                        val postModel = snapshot.getValue(PostModel::class.java)
                        if (postModel!!.publisher == pageId){
                            postList!!.add(postModel)
                        }
                }
                postAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun totalFollowers(){
        val data = FirebaseDatabase.getInstance().reference.child("Pages")
            .child(pageId).child("pageFollowers")

        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pageFollowersCount.text = snapshot.childrenCount.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun checkFollowing(){
        val data = FirebaseDatabase.getInstance().reference.child("Pages")
            .child(pageId)
            .child("pageFollowers")
        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)){
                    pageFollow_Btn_pd.text = "Unfollow"
                }else{
                    pageFollow_Btn_pd.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}