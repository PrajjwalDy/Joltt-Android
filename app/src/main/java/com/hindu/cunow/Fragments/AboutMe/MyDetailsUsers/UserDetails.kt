package com.hindu.cunow.Fragments.AboutMe.MyDetailsUsers

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Adapter.ExpAdapter
import com.hindu.cunow.Adapter.InterestAdapter_Prof
import com.hindu.cunow.Adapter.SkillAdapter
import com.hindu.cunow.Model.ESModel
import com.hindu.cunow.Model.InterestModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.fragment_user_details.experience_btn_user
import kotlinx.android.synthetic.main.fragment_user_details.gender_txt_user
import kotlinx.android.synthetic.main.fragment_user_details.skill_btn_user
import kotlinx.android.synthetic.main.fragment_user_details.view.*
import kotlinx.android.synthetic.main.my_details_fragment.experience_btn_cv
import kotlinx.android.synthetic.main.my_details_fragment.experience_btn_tv
import kotlinx.android.synthetic.main.my_details_fragment.gender_txt
import kotlinx.android.synthetic.main.my_details_fragment.interest_btn_cv
import kotlinx.android.synthetic.main.my_details_fragment.interest_btn_tv
import kotlinx.android.synthetic.main.my_details_fragment.skill_btn_cv
import kotlinx.android.synthetic.main.my_details_fragment.skill_btn_txt
import kotlinx.android.synthetic.main.my_details_fragment.view.*

class UserDetails : Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    var myInterest: List<String>? = null

    var recyclerView: RecyclerView? = null
    private var interestAdapter: InterestAdapter_Prof? = null

    private var expAdapter_Prof: ExpAdapter? = null
    private var skillAdapter: SkillAdapter? = null
    private var interestList: MutableList<InterestModel>? = null
    private var ESList: MutableList<ESModel>? = null


    //Animations
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.to_bottom_anim
        )
    }
    private val layout: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.from_bottom2
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_user_details, container, false)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("uid", "none")!!
        }

        retrieveUserData(root)

        //Skill Button
        root.skill_btn_user.setOnClickListener {
            val recyclerView: RecyclerView = root.findViewById(R.id.user_skill_rv)

            root.skill_btn_user.setCardBackgroundColor(Color.parseColor("#FF3A63"))
            root.skill_txt_user.setTextColor(Color.WHITE)

            root.experience_btn_user.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            root.experience_txt_user.setTextColor(Color.parseColor("#226880"))

            root.interest_btn_user.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            root.interest_txt_user.setTextColor(Color.parseColor("#226880"))

            //visibility of the Layouts

            root.ll_skills_user.visibility = View.VISIBLE
            root.ll_skills_user.startAnimation(layout)

            root.ll_interests_user.visibility = View.GONE
            root.ll_interests_user.startAnimation(toBottom)

            root.ll_about_user.visibility = View.GONE
            root.ll_about_user.startAnimation(toBottom)

            root.ll_experience_user.visibility = View.GONE
            root.ll_experience_user.startAnimation(toBottom)


            initView2(recyclerView)
            ESList = ArrayList()
            skillAdapter = context?.let { it1 -> SkillAdapter(it1, ESList as ArrayList<ESModel>) }
            recyclerView.adapter = skillAdapter
            loadSkills()
        }
        //Interest Button
        root.interest_btn_user.setOnClickListener {
            val recyclerView1: RecyclerView = root.findViewById(R.id.user_interest_rv)

            root.interest_btn_user.setCardBackgroundColor(Color.parseColor("#FF3A63"))
            root.interest_txt_user.setTextColor(Color.WHITE)

            root.experience_btn_user.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            root.experience_txt_user.setTextColor(Color.parseColor("#226880"))

            root.skill_btn_user.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            root.skill_txt_user.setTextColor(Color.parseColor("#226880"))


            //visibility of the Layouts
            root.ll_interests_user.visibility = View.VISIBLE
            root.ll_interests_user.startAnimation(layout)

            root.ll_about_user.visibility = View.GONE
            root.ll_about_user.startAnimation(toBottom)

            root.ll_experience_user.visibility = View.GONE
            root.ll_experience_user.startAnimation(toBottom)

            root.ll_skills_user.visibility = View.GONE
            root.ll_skills_user.startAnimation(toBottom)


            //loadData
            initView(recyclerView1, 3)
            interestList = ArrayList()
            interestAdapter = context?.let { it1 ->
                InterestAdapter_Prof(
                    it1,
                    interestList as ArrayList<InterestModel>
                )
            }
            recyclerView1.adapter = interestAdapter
            loadInterest()
        }
        //Experience button
        root.experience_btn_user.setOnClickListener {

            val recyclerView: RecyclerView = root.findViewById(R.id.user_experience_rv)

            root.experience_btn_user.setCardBackgroundColor(Color.parseColor("#FF3A63"))
            root.experience_txt_user.setTextColor(Color.WHITE)

            root.skill_btn_user.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            root.skill_txt_user.setTextColor(Color.parseColor("#226880"))

            root.interest_btn_user.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            root.interest_txt_user.setTextColor(Color.parseColor("#226880"))

            //visibility and animation of the layouts

            root.ll_experience_user.visibility = View.VISIBLE
            root.ll_experience_user.startAnimation(layout)

            root.ll_skills_user.visibility = View.GONE
            root.ll_skills_user.startAnimation(toBottom)

            root.ll_interests_user.visibility = View.GONE
            root.ll_interests_user.startAnimation(toBottom)

            root.ll_about_user.visibility = View.GONE
            root.ll_about_user.startAnimation(toBottom)



            initView(recyclerView, 2)
            ESList = ArrayList()
            expAdapter_Prof = context?.let { it1 -> ExpAdapter(it1, ESList as ArrayList<ESModel>) }
            recyclerView.adapter = expAdapter_Prof
            loadExperience()
        }

        return root
    }

    private fun retrieveUserData(root: View) {
        val dataRef = FirebaseDatabase
            .getInstance().reference
            .child("Users")
            .child(
                profileId
            )

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val users = snapshot.getValue(UserModel::class.java)
                    root.from_users.text = users!!.place
                    root.branch_user.text = users.branch
                    root.year_user.text = "Year: " + users.year
                    root.institutionName_user.text = users.college
                    root.student_course_user.text = users.course

                    if (users.male) {
                        root.genderImage_user.setImageResource(R.drawable.male)
                        gender_txt_user.text = "Male"
                        if (users.single) {
                            root.RS_Image_user.setImageResource(R.drawable.single_male)
                            root.relation_txt_user.text = "Single"
                        } else if (users.committed) {
                            root.RS_Image_user.setImageResource(R.drawable.com_male)
                            root.relation_txt_user.text = "Committed"
                        } else if (users.crush) {
                            root.RS_Image_user.setImageResource(R.drawable.crush_male)
                            root.relation_txt_user.text = "Have a Crush"
                        }
                    } else if (users.female) {
                        root.genderImage_user.setImageResource(R.drawable.female)
                        gender_txt_user.text = "Female"
                        if (users.single) {
                            root.RS_Image_user.setImageResource(R.drawable.single)
                            root.relation_txt_user.text = "Single"
                        } else if (users.committed) {
                            root.RS_Image_user.setImageResource(R.drawable.com_female)
                            root.relation_txt_user.text = "Committed"
                        } else if (users.crush) {
                            root.RS_Image_user.setImageResource(R.drawable.crush_female)
                            root.relation_txt_user.text = "Have a Crush"
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    private fun initView(recyclerView: RecyclerView, spanCount: Int) {
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, spanCount)
        recyclerView.layoutManager = linearLayoutManager
    }
    private fun initView2(recyclerView: RecyclerView) {
        recyclerView.isNestedScrollingEnabled = false
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
    }

    private fun readInterest() {
        val intRef = FirebaseDatabase.getInstance().reference.child("interests")
        intRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    interestList!!.clear()
                    for (snapshot in snapshot.children) {
                        val interest = snapshot.getValue(InterestModel::class.java)
                        for (key in myInterest!!) {
                            if (interest!!.inteID == key) {
                                interestList!!.add(interest)
                            }
                            interestList!!.reverse()
                        }
                        interestAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun loadInterest() {
        val interestList = ArrayList<InterestModel>()
        myInterest = ArrayList()
        val database = FirebaseDatabase.getInstance().reference.child("InterestUser")
            .child(profileId)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapshot in snapshot.children) {
                        (myInterest as ArrayList<String>).add(snapshot.key!!)
                    }
                    readInterest()
                    interestAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun loadSkills() {
        val skillData = FirebaseDatabase.getInstance().getReference("Skills")
            .child(profileId)
        skillData.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (snapshot in snapshot.children){
                        val data = snapshot.getValue(ESModel::class.java)
                        ESList?.add(data!!)
                    }
                    skillAdapter!!.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun loadExperience() {
        val expData = FirebaseDatabase.getInstance().getReference("Experience")
            .child(profileId)
        expData.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (snapshot in snapshot.children){
                        val data = snapshot.getValue(ESModel::class.java)
                        ESList?.add(data!!)
                    }
                    expAdapter_Prof!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

}