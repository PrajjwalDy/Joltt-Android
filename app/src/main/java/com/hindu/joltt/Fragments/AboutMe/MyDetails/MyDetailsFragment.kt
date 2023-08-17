package com.hindu.joltt.Fragments.AboutMe.MyDetails

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.cunow.databinding.MyDetailsFragmentBinding
import com.hindu.joltt.Adapter.ExpAdapter
import com.hindu.joltt.Adapter.InterestAdapter_Prof
import com.hindu.joltt.Adapter.SkillAdapter
import com.hindu.joltt.Model.UserModel
import kotlinx.android.synthetic.main.my_details_fragment.experience_btn_cv
import kotlinx.android.synthetic.main.my_details_fragment.experience_btn_tv
import kotlinx.android.synthetic.main.my_details_fragment.gender_txt
import kotlinx.android.synthetic.main.my_details_fragment.interest_btn_cv
import kotlinx.android.synthetic.main.my_details_fragment.interest_btn_tv
import kotlinx.android.synthetic.main.my_details_fragment.skill_btn_cv
import kotlinx.android.synthetic.main.my_details_fragment.skill_btn_txt
import kotlinx.android.synthetic.main.my_details_fragment.view.RS_Image_profile
import kotlinx.android.synthetic.main.my_details_fragment.view.branch_profile
import kotlinx.android.synthetic.main.my_details_fragment.view.experience_btn
import kotlinx.android.synthetic.main.my_details_fragment.view.from_profile
import kotlinx.android.synthetic.main.my_details_fragment.view.genderImage_profile
import kotlinx.android.synthetic.main.my_details_fragment.view.institutionName
import kotlinx.android.synthetic.main.my_details_fragment.view.interest_btn_cv
import kotlinx.android.synthetic.main.my_details_fragment.view.ll_about_profile
import kotlinx.android.synthetic.main.my_details_fragment.view.ll_experience
import kotlinx.android.synthetic.main.my_details_fragment.view.ll_interests
import kotlinx.android.synthetic.main.my_details_fragment.view.ll_skills
import kotlinx.android.synthetic.main.my_details_fragment.view.postBtn_btn_cv
import kotlinx.android.synthetic.main.my_details_fragment.view.relation_txt
import kotlinx.android.synthetic.main.my_details_fragment.view.skill_btn
import kotlinx.android.synthetic.main.my_details_fragment.view.student_course
import kotlinx.android.synthetic.main.my_details_fragment.view.year_profile

class MyDetailsFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    private var interestAdapter: InterestAdapter_Prof? = null
    private lateinit var viewModel: MyDetailsViewModel

    private var expAdapter_Prof: ExpAdapter? = null
    private var skillAdapter: SkillAdapter? = null

    private var _binding: MyDetailsFragmentBinding? = null
    private val binidng get() = _binding!!

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
    private val navigation: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.nav_anim
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(MyDetailsViewModel::class.java)
        _binding = MyDetailsFragmentBinding.inflate(inflater, container, false)
        val root: View = binidng.root

        //Top Function Call
        retrieveUserData(root)

        //Skill Button
        root.skill_btn.setOnClickListener {
            val recyclerView: RecyclerView = root.findViewById(R.id.profile_skill_rv)

            skill_btn_cv.setCardBackgroundColor(Color.parseColor("#FF3A63"))
            skill_btn_txt.setTextColor(Color.WHITE)

            experience_btn_cv.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            experience_btn_tv.setTextColor(Color.parseColor("#226880"))

            interest_btn_cv.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            interest_btn_tv.setTextColor(Color.parseColor("#226880"))

            //visibility of the Layouts

            root.ll_skills.visibility = View.VISIBLE
            root.ll_skills.startAnimation(layout)

            root.ll_interests.visibility = View.GONE
            root.ll_interests.startAnimation(toBottom)

            root.ll_about_profile.visibility = View.GONE
            root.ll_about_profile.startAnimation(toBottom)

            root.ll_experience.visibility = View.GONE
            root.ll_experience.startAnimation(toBottom)

            viewModel.mySkillModel!!.observe(viewLifecycleOwner, Observer {
                initView2(recyclerView)
                skillAdapter = context?.let { it1 -> SkillAdapter(it1, it) }
                recyclerView.adapter = skillAdapter
                skillAdapter!!.notifyDataSetChanged()
            })

        }

        //Interest Button
        root.interest_btn_cv.setOnClickListener {

            //Changing the color theme of the Card View
            interest_btn_cv.setCardBackgroundColor(Color.parseColor("#FF3A63"))
            interest_btn_tv.setTextColor(Color.WHITE)

            experience_btn_cv.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            experience_btn_tv.setTextColor(Color.parseColor("#226880"))

            skill_btn_cv.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            skill_btn_txt.setTextColor(Color.parseColor("#226880"))


            val recyclerView1: RecyclerView = root.findViewById(R.id.profile_interest_rv)


            //visibility of the Layouts
            root.ll_interests.visibility = View.VISIBLE
            root.ll_interests.startAnimation(layout)

            root.ll_about_profile.visibility = View.GONE
            root.ll_about_profile.startAnimation(toBottom)

            root.ll_experience.visibility = View.GONE
            root.ll_experience.startAnimation(toBottom)

            root.ll_skills.visibility = View.GONE
            root.ll_skills.startAnimation(toBottom)


            //loadData
            viewModel.myInterestModel!!.observe(viewLifecycleOwner, Observer {
                initView(recyclerView1, 3)
                interestAdapter = context?.let { it1 -> InterestAdapter_Prof(it1, it) }
                recyclerView1.adapter = interestAdapter
                interestAdapter!!.notifyDataSetChanged()
            })
        }

        //Experience button
        root.experience_btn.setOnClickListener {

            val recyclerView: RecyclerView = root.findViewById(R.id.profile_experience_rv)

            experience_btn_cv.setCardBackgroundColor(Color.parseColor("#FF3A63"))
            experience_btn_tv.setTextColor(Color.WHITE)

            interest_btn_cv.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            interest_btn_tv.setTextColor(Color.parseColor("#226880"))

            skill_btn_cv.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            skill_btn_txt.setTextColor(Color.parseColor("#226880"))

            //visibility and animation of the layouts

            root.ll_experience.visibility = View.VISIBLE
            root.ll_experience.startAnimation(layout)

            root.ll_skills.visibility = View.GONE
            root.ll_skills.startAnimation(toBottom)

            root.ll_interests.visibility = View.GONE
            root.ll_interests.startAnimation(toBottom)

            root.ll_about_profile.visibility = View.GONE
            root.ll_about_profile.startAnimation(toBottom)

            viewModel.myExpModel!!.observe(viewLifecycleOwner, Observer {
                initView(recyclerView, 2)
                expAdapter_Prof = context?.let { it1 -> ExpAdapter(it1, it) }
                recyclerView.adapter = expAdapter_Prof
                expAdapter_Prof!!.notifyDataSetChanged()
            })

        }

        root.postBtn_btn_cv.setOnClickListener {

            Navigation.findNavController(root)
                .navigate(R.id.action_myDetailsFragment_to_myPostsFragemt)

        }

        return root

    }

    //Retrieve User Data
    private fun retrieveUserData(root: View) {
        val dataRef = FirebaseDatabase
            .getInstance().reference
            .child("Users")
            .child(
                FirebaseAuth
                    .getInstance()
                    .currentUser!!.uid
            )

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val users = snapshot.getValue(UserModel::class.java)
                    root.from_profile.text = users?.place
                    root.institutionName.text = users?.college
                    root.student_course.text= users?.course
                    root.branch_profile.text = users?.branch
                    root.year_profile.text = "Year: " + users?.year

                    if (users?.male == true) {
                        root.genderImage_profile.setImageResource(R.drawable.male)
                        gender_txt.text = "Male"
                        if (users.single) {
                            root.RS_Image_profile.setImageResource(R.drawable.single_male)
                            root.relation_txt.text = "Single"
                        } else if (users.committed) {
                            root.RS_Image_profile.setImageResource(R.drawable.com_male)
                            root.relation_txt.text = "Committed"
                        } else if (users.crush) {
                            root.RS_Image_profile.setImageResource(R.drawable.crush_male)
                            root.relation_txt.text = "Have a Crush"
                        }
                    } else if (users?.female == true) {
                        root.genderImage_profile.setImageResource(R.drawable.female)
                        gender_txt.text = "Female"
                        if (users.single) {
                            root.RS_Image_profile.setImageResource(R.drawable.single)
                            root.relation_txt.text = "Single"
                        } else if (users.committed) {
                            root.RS_Image_profile.setImageResource(R.drawable.com_female)
                            root.relation_txt.text = "Committed"
                        } else if (users.crush) {
                            root.RS_Image_profile.setImageResource(R.drawable.crush_female)
                            root.relation_txt.text = "Have a Crush"
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    //Global Init function for RecyclerView
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

}