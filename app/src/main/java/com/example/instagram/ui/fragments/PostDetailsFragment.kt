package com.example.instagram.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.instagram.R
import com.example.instagram.databinding.FragmentPostDetailsBinding
import com.example.instagram.model.Post
import com.example.instagram.model.viewModels.HomeFragViewModel
import com.example.instagram.ui.adapter.PostsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PostDetailsFragment : Fragment() {

    private lateinit var binding : FragmentPostDetailsBinding
    private lateinit var postsList : ArrayList<Post>
    private lateinit var postId : String
    private lateinit var viewModel : HomeFragViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostDetailsBinding.inflate(inflater)

        viewModel = ViewModelProviders.of(this).get(HomeFragViewModel::class.java)

        val editor = context!!.getSharedPreferences("PREFS" , Context.MODE_PRIVATE)
        if (editor!=null){
            postId = editor.getString("postId" , "").toString()
        }

        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.recyclerViewPostDetails.adapter = PostsAdapter(context!!)

        retrievePosts()

        return binding.root
    }

    private fun retrievePosts() {
        postsList = ArrayList()
        val postsRef = FirebaseDatabase.getInstance().reference.child("posts")
            .child(postId)

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                postsList.clear()

                if (snapshot.exists()) {
                    val post = snapshot.getValue(Post::class.java)
                    postsList.add(post!!)

                    viewModel._postList.value = postsList.toList()
                }
            }
        })
    }

}