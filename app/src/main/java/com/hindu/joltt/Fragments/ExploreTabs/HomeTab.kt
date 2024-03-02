package com.hindu.joltt.Fragments.ExploreTabs

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Activity.FeedbackActivity
import com.hindu.joltt.Adapter.UserAdapter
import com.hindu.joltt.Fragments.Pages.PagesTabActivity
import com.hindu.joltt.Model.FeatureModel
import com.hindu.joltt.Model.UserModel
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener

class HomeTab : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<UserModel>? = null
    private var checker = "Name"

    private lateinit var ll_confessionRoom: LinearLayout
    private lateinit var ll_post: LinearLayout
    private lateinit var ll_govt_schemes: LinearLayout
    private lateinit var ll_clubs: LinearLayout
    private lateinit var ll_community: LinearLayout
    private lateinit var ll_pages: LinearLayout
    private lateinit var ll_courses: LinearLayout
    private lateinit var ll_people: LinearLayout
    private lateinit var ll_feedback: LinearLayout


    //Image Carousel

    var imageArray: ArrayList<String> = ArrayList()
    var carouselView: CarouselView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_home_tab, container, false)

        ll_confessionRoom = root.findViewById(R.id.ll_confessionRoom)
        ll_post = root.findViewById(R.id.ll_post)
        ll_govt_schemes = root.findViewById(R.id.ll_govt_schemes)
        ll_clubs = root.findViewById(R.id.ll_clubs)
        ll_community = root.findViewById(R.id.ll_community)
        ll_pages = root.findViewById(R.id.ll_pages)
        ll_courses = root.findViewById(R.id.ll_courses)
        ll_people = root.findViewById(R.id.ll_people)
        ll_feedback = root.findViewById(R.id.ll_feedback)




        ll_confessionRoom.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_confessionRoomFragment)
        }


//




        //Image Carousel loading image

        carouselView = root.findViewById(R.id.carouselview)


        var imageListener = ImageListener { postion, imageView ->

            context?.let {
                Glide.with(it)
                    .load(imageArray[postion])
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(imageView)
            }
        }

        loadCarouselImage(imageListener)

        carouselView?.apply {
            setImageListener(imageListener)
        }


        ll_post.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_publicPostFragement)
        }

        ll_govt_schemes.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_schemesFragment)
        }

        ll_clubs.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_jobsFragment)
        }

        ll_pages.setOnClickListener {
            startActivity(Intent(context, PagesTabActivity::class.java))
        }

        ll_courses.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_coursesFragment)
        }

        ll_people.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_peopleFragment)
        }

        ll_feedback.setOnClickListener {
            startActivity(Intent(context, FeedbackActivity::class.java))
        }

        ll_community.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_dashboard_to_flash)
        }

        return root
    }






    private fun loadCarouselImage(imageListener: ImageListener) {
        val dbRef = FirebaseDatabase.getInstance().reference.child("featureImage")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot in snapshot.children) {
                    val imageData = snapshot.getValue(FeatureModel::class.java)
                    if (imageData != null) {
                        imageArray.add(imageData.cImageLink!!)
                    }
                }
                carouselView!!.pageCount = imageArray.size
                carouselView!!.setImageListener(imageListener)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}