package com.example.instagram.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.MainActivity
import com.example.instagram.R
import com.example.instagram.databinding.PostsLayoutBinding
import com.example.instagram.model.Post
import com.example.instagram.model.User
import com.example.instagram.ui.activities.CommentsActivity
import com.example.instagram.ui.activities.ShowUsersActivity
import com.example.instagram.ui.fragments.PostDetailsFragment
import com.example.instagram.ui.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostsAdapter(private val context: Context ): ListAdapter<Post , PostsAdapter.PostsViewHolder>(DiffCallback){

private val firebaseUser = FirebaseAuth.getInstance().currentUser

    class PostsViewHolder(val binding : PostsLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post : Post){
            binding.post = post
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        return PostsViewHolder(PostsLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        var post : Post = getItem(position)
        holder.bind(post)


        val profileImage = holder.itemView.findViewById<CircleImageView>(R.id.user_profile_image_search)
        val profileName = holder.itemView.findViewById<TextView>(R.id.user_name_search)
        val postPublisher = holder.itemView.findViewById<TextView>(R.id.publisher)
        val likeImage =holder.itemView.findViewById<ImageView>(R.id.post_image_like_btn)
        val likes =holder.itemView.findViewById<TextView>(R.id.likes)
        val commentsBtn = holder.itemView.findViewById<ImageView>(R.id.post_image_comment_btn)
        val comments =holder.itemView.findViewById<TextView>(R.id.comments)
        val saveImage =holder.itemView.findViewById<ImageView>(R.id.post_save_comment_btn)
        val postImage =holder.itemView.findViewById<ImageView>(R.id.post_image_home)

        publisherInfo(profileImage , profileName , postPublisher , post.publisher)

         isLikes(post.postId , likeImage)
         numberOfLikes(likes , post.postId)
         getTotalComments(comments , post.postId)
         checkSavedStatus(post.postId , saveImage)
        /**
         * click on profile image
         */
        profileImage.setOnClickListener {

            val pref = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId" , post.publisher)
            pref.apply()

            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container , ProfileFragment()).commit()
        }
        /**
         * click on publisher
         */
        postPublisher.setOnClickListener {
            val pref = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId" , post.publisher)
            pref.apply()

            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container , ProfileFragment()).commit()
        }
        /**
         * click on postImage
         */
        postImage.setOnClickListener {

            val editor = context.getSharedPreferences("PREFS" , Context.MODE_PRIVATE).edit()
            editor.putString("postId" , post.postId).apply()

            (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container
                    , PostDetailsFragment()).commit()
        }
        /**
         * click on likeImage
         */
        likeImage.setOnClickListener {
            if (likeImage.tag =="Like"){
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.postId)
                        .child(firebaseUser!!.uid).setValue(true)
                addNotification(post.publisher , post.postId)

            }else{
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.postId)
                        .child(firebaseUser!!.uid).removeValue()

                val intent =Intent(context , MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
            }
        }
        /**
         * when user click on comments btn
         */
        commentsBtn.setOnClickListener {
            val intent =Intent(context , CommentsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // add extras
            intent.putExtra("postId" , post.postId)
            intent.putExtra("publisherId" , post.publisher)

            context.startActivity(intent)
        }
        /**
         *  click on text likes
         */
     likes.setOnClickListener {
         var intent = Intent(context , ShowUsersActivity::class.java)

         intent.putExtra("id" , post.postId)
         intent.putExtra("title" , "likes")

         context.startActivity(intent)
     }
        /**
         * click on save Image
         */
        saveImage.setOnClickListener {
            if (saveImage.tag=="Save"){
                FirebaseDatabase.getInstance().reference.
                child("Saves").
                child(firebaseUser!!.uid)
                    .child(post.postId).setValue(true)
            }else{
                FirebaseDatabase.getInstance().reference.
                child("Saves").
                child(firebaseUser!!.uid)
                    .child(post.postId).removeValue()
            }
        }

        comments.setOnClickListener {
            val intent =Intent(context , CommentsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // add extras
            intent.putExtra("postId" , post.postId)
            intent.putExtra("publisherId" , post.publisher)

            context.startActivity(intent)
        }

    }

    private fun getTotalComments(comments: TextView?, postId: String) {

        val commentsRref = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)
        commentsRref.addValueEventListener(object  : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    comments!!.text = "view all" +  snapshot.childrenCount.toString() + " comment"
                }
            } })
    }

    private fun numberOfLikes(likes: TextView?, postId: String) {
    val likesRref = FirebaseDatabase.getInstance().reference.child("Likes").child(postId)
        likesRref.addValueEventListener(object  : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
             if (snapshot.exists()){
                 likes!!.text = snapshot.childrenCount.toString() + " likes"
             }
            } })
    }

    private fun isLikes(postId: String, likeImage: ImageView?) {

        FirebaseDatabase.getInstance().reference.child("Likes").child(postId)
                .addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                     if (snapshot.child(firebaseUser!!.uid.toString()).exists()){
                         likeImage!!.setImageResource(R.drawable.heart_clicked)
                         likeImage.tag ="Liked"
                     }else{
                         likeImage!!.setImageResource(R.drawable.heart_not_clicked)
                         likeImage.tag ="Like"
                     }
                    }
                })

    }

    private fun publisherInfo(profileImage: CircleImageView?, profileName: TextView?, postPublisher: TextView?, publisherId: String) {
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(publisherId)
        userRef.addValueEventListener(object  : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()){
                var user = snapshot.getValue(User::class.java)
               Picasso.get().load(user!!.image).placeholder(R.drawable.profile).into(profileImage)
                profileName!!.text = user.userName
                postPublisher!!.text=user.fullName
            }
            }

        })
    }

    private fun checkSavedStatus(postId: String , imageView: ImageView){
       val saveRef = FirebaseDatabase.getInstance().reference.
        child("Saves").
        child(firebaseUser!!.uid)

        saveRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
             if (snapshot.child(postId).exists()){
                 imageView.setImageResource(R.drawable.save_large_icon)
                 imageView.tag= "Saved"
             }else{
                 imageView.setImageResource(R.drawable.save_unfilled_large_icon)
                 imageView.tag= "Save" }
            } })

    }

    companion object DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.postId == newItem.postId
        }
    }
    private fun addNotification(userId : String , postId : String){

        val notificationsRef = FirebaseDatabase.getInstance().reference.child("notifications")
                .child(userId)

        val notificationMap = HashMap<String , Any>()
        notificationMap.put("userId" , firebaseUser!!.uid)
        notificationMap.put("postId" , postId)
        notificationMap.put("text" , "liked your post")
        notificationMap.put("isPost" , true)

        notificationsRef.push().setValue(notificationMap)

    }
}