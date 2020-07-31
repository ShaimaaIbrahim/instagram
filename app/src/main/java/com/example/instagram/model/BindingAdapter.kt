package com.example.instagram.model


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.R
import com.example.instagram.ui.adapter.*
import com.squareup.picasso.Picasso

@BindingAdapter("userName")
fun bindUserName(textView: TextView , userName: String){
 textView.text = userName
}

@BindingAdapter("fullName")
fun bindfullName(textView: TextView , fullName: String){
textView.text = fullName
}

@BindingAdapter("imageUrl")
fun bindimageUrl(imageView: ImageView , imageUrl: String){
 Picasso.get().load(imageUrl).placeholder(R.drawable.profile).into(imageView)
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView , listUsers: List<User>?){
 val adapter = recyclerView.adapter as UserAdapter
 adapter.submitList(listUsers)
}

@BindingAdapter("imagePost")
fun bindPostImage(imageView: ImageView , imagepost: String){
 if (!imagepost.isEmpty()){
  Picasso.get().load(imagepost).placeholder(R.drawable.profile).into(imageView)

 }
}

@BindingAdapter("postDes")
fun bindPostDes(textView: TextView, description: String){
 if (description.equals("")){
  textView.visibility=View.GONE
 }else{
  textView.visibility=View.VISIBLE
  textView.text = description
 } }

@BindingAdapter("listPosts")
fun bindPostRecyclerView(recyclerView: RecyclerView , listPosts: List<Post>?){
 val adapter = recyclerView.adapter as PostsAdapter
 adapter.submitList(listPosts)
}

@BindingAdapter("listStory")
fun bindStoryRecyclerView(recyclerView: RecyclerView , listStory: List<Story>?){
 val adapter = recyclerView.adapter as StoryAdapter
 adapter.submitList(listStory)
}

@BindingAdapter("listComments")
fun bindCommentsRecyclerView(recyclerView: RecyclerView , listComment: List<Comment>?){
 val adapter = recyclerView.adapter as CommentsAdapter
 adapter.submitList(listComment)
}

@BindingAdapter("listNotifications")
fun bindNotificationsRecyclerView(recyclerView: RecyclerView , listNotification: List<Notification>?){
 val adapter = recyclerView.adapter as NotificationAdapter
 adapter.submitList(listNotification)
}

@BindingAdapter("listImages")
fun bindImagesRecyclerView(recyclerView: RecyclerView , listImages: List<Post>?){
 val adapter = recyclerView.adapter as MyImagesAdapter
 adapter.submitList(listImages)
}
@BindingAdapter("imageGrid")
fun bindGridImage(imageView: ImageView , imageGrid: String){
 if (imageGrid.isEmpty() || imageGrid==""){
  imageView.setImageResource(R.drawable.profile)
 }else{
 Picasso.get().load(imageGrid).placeholder(R.drawable.profile).into(imageView)
}}

@BindingAdapter("commentTxt")
fun bindCommentTxt(textView: TextView , comment: String){
 textView.text = comment
}