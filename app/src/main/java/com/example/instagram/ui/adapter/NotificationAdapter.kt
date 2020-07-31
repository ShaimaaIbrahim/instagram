package com.example.instagram.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.R
import com.example.instagram.databinding.NotificationItemLayoutBinding
import com.example.instagram.model.Notification
import com.example.instagram.model.User
import com.example.instagram.ui.fragments.PostDetailsFragment
import com.example.instagram.ui.fragments.ProfileFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class NotificationAdapter(private  var context: Context ) : androidx.recyclerview.widget.ListAdapter<Notification , NotificationAdapter.viewHolder>(DiffCallback) {

    class viewHolder(var binding : NotificationItemLayoutBinding ) : RecyclerView.ViewHolder(binding.root) {

        fun bind( notification: Notification){
            binding.notification= notification
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(NotificationItemLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

       var notification = getItem(position)

        holder.bind(notification)

        Log.e("insta" , notification.getIsPost().toString() + " " + position.toString())
        /**
         *  clicking on itemView of notification
         */
        holder.itemView.setOnClickListener {

            if (notification.getIsPost()){

                val editor = context.getSharedPreferences("PREFS" , Context.MODE_PRIVATE).edit()
                editor.putString("postId" , notification.getPostId()).apply()

                 (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container
                         , PostDetailsFragment()).commit()
            }
            else{

                val pref = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("profileId" , notification.getUserId())
                pref.apply()

                (context as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container , ProfileFragment()).commit()
            }
        }

        var profileImage = holder.itemView.findViewById<CircleImageView>(R.id.notification_profile_image)
        var username = holder.itemView.findViewById<TextView>(R.id.username_notification)
        var textNoti = holder.itemView.findViewById<TextView>(R.id.comment_notification)
        var postImage = holder.itemView.findViewById<ImageView>(R.id.notification_post_image)

        if (notification.getText().equals("started following you")){
            textNoti.text = "started following you"
        }
        else if (notification.getText().equals("liked your post")){
            textNoti.text = "liked your post"
        }
        else if (notification.getText().equals("commented : ")){
            textNoti.text = notification.getText().replace("commented : " , "commented : ")
        }
        else{
            textNoti.text = notification.getText()
        }



        userInfo(notification , profileImage , username)

        if(notification.getIsPost()){
            getPostImage(notification.getPostId() , postImage)
            postImage.visibility = View.VISIBLE
        }else{
           postImage.visibility = View.GONE

        }

    }

    companion object DiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.getUserId() == newItem.getUserId()
        }
    }

    private fun userInfo(notification: Notification , profileImage : CircleImageView , username : TextView){

        val userRef = FirebaseDatabase.getInstance().reference.child("users")
                .child(notification.getUserId())

        userRef.addValueEventListener(object  : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: User = snapshot.getValue(User::class.java)!!

                    username.text = user.userName

                    Picasso.get().load(user.image).placeholder(R.drawable.profile).into(profileImage)
                }
            } })
    }


    private fun getPostImage(postId : String , postImageView: ImageView){

        val postRef = FirebaseDatabase.getInstance().reference.child("posts")
                .child(postId).child("postImage")

        postRef.addValueEventListener(object  : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val postImage : String = snapshot.value.toString()


                    Picasso.get().load(postImage).placeholder(R.drawable.profile).into(postImageView)

                } } }) }
}