package com.example.instagram.model.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.instagram.model.Comment
import com.example.instagram.model.Post

class CommentActViewModel : ViewModel(){

    val _commentList =  MutableLiveData<List<Comment>>()
    val commentList : LiveData<List<Comment>>
    get() = _commentList
}