package com.example.instagram.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.instagram.R
import com.example.instagram.databinding.FragmentProfileBinding
import com.example.instagram.model.Grid
import com.example.instagram.model.Post
import com.example.instagram.model.User
import com.example.instagram.model.viewModels.ProfileViewModel
import com.example.instagram.ui.activities.AccountSettingsActivity
import com.example.instagram.ui.activities.ShowUsersActivity
import com.example.instagram.ui.adapter.MyImagesAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

private lateinit var binding : FragmentProfileBinding
private lateinit var profileId : String
private lateinit var firebaseUser: FirebaseUser
private lateinit var gridList : ArrayList<Grid>
    private lateinit var postsList : ArrayList<Post>
    private lateinit var savedPostsList : ArrayList<Post>
    private lateinit var savesImages : ArrayList<String>
    private lateinit var viewModel : ProfileViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater)

        viewModel=ViewModelProviders.of(this).get(ProfileViewModel::class.java)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        /**
         * set initial and default information
         */
        DefaultUserInfo()

        val pref = context!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
       if (pref!=null){
        this.profileId = pref.getString("profileId" , "none")!!
       }
        /**
         *
         */
        if (profileId==firebaseUser.uid){
        binding.editAccountSettingBtn.text = "Edit Profile"

        }
        else if (profileId!=firebaseUser.uid){
        checkFollowAndFollowingButtonState()
        }

        /**
         * click on total followers
         */
        binding.totalFollowers.setOnClickListener {

            var intent = Intent(context , ShowUsersActivity::class.java)

            intent.putExtra("id" , profileId)
            intent.putExtra("title" , "followers")

            startActivity(intent)
        }
        /**
         * click on total following
         */
        binding.totalFollowing.setOnClickListener {

            var intent = Intent(context , ShowUsersActivity::class.java)

            intent.putExtra("id" , profileId)
            intent.putExtra("title" , "following")

            startActivity(intent)
        }
        /**
         * click on editAccount setting
         */
        binding.editAccountSettingBtn.setOnClickListener {
            val getButtonText = binding.editAccountSettingBtn.text.toString()

            when{

            getButtonText==  "Edit Profile" ->   startActivity(Intent(context, AccountSettingsActivity::class.java))

            getButtonText==    "Follow" -> {
                binding.editAccountSettingBtn.text="Following"

                firebaseUser!!.uid.let {
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(it.toString()).child("Following").child(profileId)
                        .setValue(true)
            }
                    firebaseUser!!.uid.let {
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(profileId).child("Followers").child(it.toString())
                            .setValue(true)
                }
                addNotification()
            }
            getButtonText=="Following" -> {
                binding.editAccountSettingBtn.text="Follow"
                firebaseUser!!.uid.let {
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(it.toString()).child("Following").child(profileId)
                        .removeValue()
                }
                firebaseUser!!.uid.let {
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(profileId).child("Followers").child(it.toString())
                        .removeValue()
                }
            }
            }}
        /**
         * default visible of recyclers
         */
        binding.recyclerViewSaavePic.visibility = View.GONE
        binding.recyclerViewUploadPic.visibility  = View.VISIBLE

        binding.imagesSaveBtn.setOnClickListener {
          binding.recyclerViewSaavePic.visibility = View.VISIBLE
          binding.recyclerViewUploadPic.visibility  = View.GONE
        }

        binding.imagesGridViewBtn.setOnClickListener {
            binding.recyclerViewSaavePic.visibility = View.GONE
            binding.recyclerViewUploadPic.visibility  = View.VISIBLE
        }

        checkFollowers()
        checkFollowing()
        userInfo()
        myPhotos()
        getTotalNumberOfPosts()
        mySaves()

        /**
         * recyclerView for uploaded  images
         */
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.recyclerViewUploadPic.adapter= MyImagesAdapter(context!!)

        /**
         * recyclerView for saved  images
         */
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.recyclerViewSaavePic.adapter= MyImagesAdapter(context!!)

        return binding.root
    }

    private fun mySaves() {
        savesImages= ArrayList()
        val saveRef =FirebaseDatabase.getInstance().reference.child("Saves")
            .child(firebaseUser.uid)
        saveRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot.exists()){
                   savesImages.clear()
                   for (sh in snapshot.children){
                       savesImages.add(sh.key!!)

                   }
                   readSavedImageData()
               }
            } }) }

    private fun readSavedImageData() {

        savedPostsList= ArrayList()
      val postsRef = FirebaseDatabase.getInstance().reference.child("posts")
          postsRef.addValueEventListener(object : ValueEventListener{
              override fun onCancelled(error: DatabaseError) {
              }
              override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot.exists()){
                   savedPostsList.clear()
                   for (sh in snapshot.children){
                       val post = sh.getValue(Post::class.java)
                       for (key in savesImages){
                           if (post!!.postId.equals(key)){
                               savedPostsList.add(post!!)
                           }
                       }

                   }
                   viewModel._imageSavedList.value = savedPostsList
               } }

          })
    }

    /**
     * set btn in profile
     */
    private fun checkFollowAndFollowingButtonState() {
        val followingReference =
            firebaseUser!!.uid.let {
                FirebaseDatabase.getInstance().reference.child("Follow")
                    .child(it.toString()).child("Following")
            }
        if (followingReference!=null){
        followingReference
            .addValueEventListener(object  : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(profileId).exists()){
                        binding.editAccountSettingBtn.text = "Following"
                    }else{
                        binding.editAccountSettingBtn.text = "Follow"
                    }
            } }) } }

    /**
     * put total followers
     */
    private fun checkFollowers() {
        val followersReference = FirebaseDatabase.getInstance().reference.
        child("Follow")
                    .child(profileId).child("Followers")

        followersReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()){
                binding.totalFollowers.text = snapshot.childrenCount.toString()
            }
            }
        })
    }

    /**
     * put total following
     */
    private fun checkFollowing() {
        val followingReference =  FirebaseDatabase.getInstance().reference.
        child("Follow")
                    .child(profileId).child("Following")

        followingReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    binding.totalFollowing.text = snapshot.childrenCount.toString()
                } } }) }

    /**
     * put users information
     */

    private fun DefaultUserInfo(){
        val userRef = FirebaseDatabase.getInstance().reference.child("users")
            .child(firebaseUser.uid)

        userRef.addValueEventListener(object  : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
//                if (context !=null){
//                    return
//                }
                if (snapshot.exists()){
                    val user : User = snapshot.getValue(User::class.java)!!

                    binding.fullNameProfileFrag.text = user.fullName
                    binding.pioProfileFrag.text = user.bio
                    binding.profileFragmentUsername.text = user.userName
                    Picasso.get().load(user.image).placeholder(R.drawable.profile).into(binding.proImageProfileFrag)

                }

            }

        })
    }
    private fun userInfo(){
        val userRef = FirebaseDatabase.getInstance().reference.child("users")
            .child(profileId)

        userRef.addValueEventListener(object  : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
//                if (context !=null){
//                    return
//                }
                if (snapshot.exists()){
                    val user : User = snapshot.getValue(User::class.java)!!

                    binding.fullNameProfileFrag.text = user.fullName
                    binding.pioProfileFrag.text = user.bio
                    binding.profileFragmentUsername.text = user.userName
                    Picasso.get().load(user.image).placeholder(R.drawable.profile).into(binding.proImageProfileFrag)

                }

            }

        })
    }

    private fun myPhotos(){
        postsList = ArrayList()
        var postRef = FirebaseDatabase.getInstance().reference.child("posts")
               postRef.addValueEventListener(object : ValueEventListener{
                   override fun onCancelled(error: DatabaseError) {
                   }
                   override fun onDataChange(snapshot: DataSnapshot) {
                       if (snapshot.exists()){
                           postsList.clear()
                           for (po in snapshot.children){
                               var post = po.getValue(Post::class.java)

                               if (post!!.publisher.equals(profileId)){
                                //   var grid = Grid(post.postImage)
                               //    var grid = Grid("https://firebasestorage.googleapis.com/v0/b/socialmediaapp-733da.appspot.com/o/Posts%20Pictures%2F1595202074065.jpg?alt=media&token=7f24c512-1437-4168-a511-2292f21eb8a2")
                               //    grid.imageUrl= "https://firebasestorage.googleapis.com/v0/b/socialmediaapp-733da.appspot.com/o/Posts%20Pictures%2F1595202074065.jpg?alt=media&token=7f24c512-1437-4168-a511-2292f21eb8a2"
                             //      grid.imageUrl= post.postImage.toString()
                                //   gridList.add(grid)
                                   postsList.add(post)
                               }
                           }
                           Collections.reverse(postsList)
                           viewModel._imagesList.value = postsList.toList()
                       }
                   }

               })

    }
    private fun getTotalNumberOfPosts(){
        var postRef = FirebaseDatabase.getInstance().reference.child("posts")
        postRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
             if (snapshot.exists()){
                 var postCounter =0
                 for (sh in snapshot.children){
                     val post = sh.getValue(Post::class.java)
                     if (post!!.publisher.equals(profileId)){
                         ++postCounter
                     }
                 }
                 binding.totalPosts.text = postCounter.toString()
             }
            } })
    }

    override fun onStop() {
        super.onStop()
        val pref = context!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        pref.putString("profileId" , firebaseUser.uid)
        pref.apply()

    }

    override fun onPause() {
        super.onPause()
        val pref = context!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        pref.putString("profileId" , firebaseUser.uid)
        pref.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        pref.putString("profileId" , firebaseUser.uid)
        pref.apply()
    }
    private fun addNotification(){

        val notificationsRef = FirebaseDatabase.getInstance().reference.child("notifications")
                .child(profileId)

        val notificationMap = HashMap<String , Any>()
        notificationMap.put("userId" , firebaseUser!!.uid)
        notificationMap.put("postId" , "")
        notificationMap.put("text" , " started following you!")
        notificationMap.put("isPost" , false)

        notificationsRef.push().setValue(notificationMap)

    }

}
