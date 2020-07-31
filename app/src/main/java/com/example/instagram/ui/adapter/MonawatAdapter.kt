package com.example.instagram.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.R
import com.example.instagram.databinding.MediaLayoutBinding
import com.example.instagram.model.Comment
import com.squareup.picasso.Picasso

class MonawatAdapter(private var context: Context, private var mediaList : ArrayList<String>) : ListAdapter<String, MonawatAdapter.viewHolder>(DiffCallback)  {


    class viewHolder(var binding: MediaLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mediaUrl : String){

           Log.e("nooo" , "attatched")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
      return viewHolder(MediaLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        var media = mediaList.get(position)
         holder.bind(media)



       var imageMedia=  holder.itemView.findViewById<ImageView>(R.id.image_media)

        Picasso.get().load(media).placeholder(R.drawable.profile).into(imageMedia)

        if (media.contains("youtube") || media.contains("watch")){

        }else
        {

        }

    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.length == newItem.length
        }
    }
}