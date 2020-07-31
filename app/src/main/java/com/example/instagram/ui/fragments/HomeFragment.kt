package com.example.instagram.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagram.R
import com.example.instagram.databinding.FragmentHomeBinding
import com.example.instagram.model.Post
import com.example.instagram.model.Story
import com.example.instagram.model.viewModels.HomeFragViewModel
import com.example.instagram.ui.adapter.PostsAdapter
import com.example.instagram.ui.adapter.StoryAdapter
import com.google.firebase.FirebaseAppLifecycleListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var viewModel : HomeFragViewModel
    private lateinit var followingList : ArrayList<String>
    private lateinit var postsList : ArrayList<Post>
    private lateinit var storyList : ArrayList<Story>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       binding = FragmentHomeBinding.inflate(inflater)

        viewModel = ViewModelProviders.of(this).get(HomeFragViewModel::class.java)

        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.recyclerViewHome.adapter = PostsAdapter(context!!)
        binding.recyclerViewStory.adapter = StoryAdapter(context!!)

        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.recyclerViewStory.setHasFixedSize(true)
        binding.recyclerViewStory.layoutManager= LinearLayoutManager(context , LinearLayoutManager.HORIZONTAL , false)


        checkFollowings()

        return binding.root
    }

    private fun checkFollowings() {

        followingList = ArrayList()

        val followRef = FirebaseDatabase.getInstance().reference.child("Follow")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child("Following")

        followRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
              if (snapshot.exists()){
                  followingList.clear()
                  for (sh in snapshot.children){
                      sh.key.let {
                          followingList.add(it!!)
                      } }
                  retrievePosts()
                  retrieveStories()
              }
            } }) }


    private fun retrieveStories() {
        storyList = ArrayList()
        val storyRef = FirebaseDatabase.getInstance().reference.child("story")

        storyRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {

              val timeCurrent = System.currentTimeMillis()
               storyList.clear()
                storyList.add(Story("", 0 , 0 ,"" , ""))

                for (id in followingList){

                    var countStory = 0
                    var story : Story ?= null

                    for (sh in snapshot.child(id).children){

                        story = sh.getValue(Story::class.java)

                        if (timeCurrent> story!!.getTimeStar()!! && timeCurrent< story.getTimeEnd()!!){
                            countStory++

                        }
                    }
                    if (countStory>0){
                        storyList.add(story!!)
                    }
                }
                viewModel._storyList.value = storyList.toList()

            }
    }) }

    private fun retrievePosts() {
        postsList = ArrayList()
 val postsRef = FirebaseDatabase.getInstance().reference.child("posts")

        postsRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                      postsList.clear()
                for (sh in snapshot.children){

                    val post = sh.getValue(Post::class.java)

                    for (userID in followingList){

                        if (post!!.publisher==userID.toString()){
                               postsList.add(post)
                            Log.e("ahmed" , post.description.toString())
                        }
                    }
                }

                viewModel._postList.value =postsList.toList()
            }
        }) }


}