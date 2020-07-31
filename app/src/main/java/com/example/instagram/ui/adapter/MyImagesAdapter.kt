package com.example.instagram.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.MainActivity
import com.example.instagram.R
import com.example.instagram.databinding.ImageItemLayoutBinding
import com.example.instagram.databinding.PostsLayoutBinding
import com.example.instagram.model.Grid
import com.example.instagram.model.Post
import com.example.instagram.ui.adapter.MyImagesAdapter.*
import com.example.instagram.ui.fragments.PostDetailsFragment

class MyImagesAdapter (private val context: Context ): androidx.recyclerview.widget.ListAdapter<Post , MyImagesAdapter.MyImagesViewHolder>(DiffCallback) {


    class MyImagesViewHolder(val binding : ImageItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post : Post){
           binding.post = post
        binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyImagesViewHolder {
     return MyImagesViewHolder(ImageItemLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MyImagesViewHolder, position: Int) {
        val post : Post = getItem(position)
           holder.bind(post)

        holder.itemView.findViewById<ImageView>(R.id.image_grid_item).setOnClickListener {
           val editor = context.getSharedPreferences("PREFS" , Context.MODE_PRIVATE).edit()
            editor.putString("postId" , post.postId).apply()

            (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container
            , PostDetailsFragment()).commit()


        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.postId == newItem.postId
        }
    }


}