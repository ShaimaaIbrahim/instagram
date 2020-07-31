package com.example.instagram.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.R
import com.example.instagram.databinding.CommentLayoutBinding
import com.example.instagram.model.Comment
import com.example.instagram.model.Post
import com.example.instagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentsAdapter (private var context: Context ): ListAdapter<Comment , CommentsAdapter.CommentsViewHolder>(DiffCallback){

    private var firebaseUser = FirebaseAuth.getInstance().currentUser!!

    class CommentsViewHolder(val binding : CommentLayoutBinding): RecyclerView.ViewHolder(binding.root) {

       fun bind(comment: Comment){
          binding.comment = comment
          binding.executePendingBindings()
       }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder(CommentLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
       var comment = getItem(position)
        holder.bind(comment)

        var user_name : TextView = holder.itemView.findViewById(R.id.user_name_comment)
        var user_image : CircleImageView = holder.itemView.findViewById(R.id.user_profile_image_comment)

            getUserInformation(user_name , user_image , comment.publisher)

    }

    private fun getUserInformation(userName: TextView, userImage: CircleImageView, publisherId: String) {
        val userRef = FirebaseDatabase.getInstance().reference.child("users")
                .child(publisherId)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
             if (snapshot.exists()){
                 val user = snapshot.getValue(User::class.java)
                 userName.text=user!!.userName
                 Picasso.get().load(user.image).placeholder(R.drawable.profile).into(userImage)
             }
            }
        })
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.publisher == newItem.publisher
        }
    }
}