package com.example.instagram.model.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.instagram.model.Post
import com.example.instagram.model.Story
import java.nio.file.attribute.PosixFileAttributeView

class HomeFragViewModel  : ViewModel(){

    val _postList =  MutableLiveData<List<Post>>()
    val postList : LiveData<List<Post>>
    get() = _postList


    val _storyList =  MutableLiveData<List<Story>>()
    val storyList : LiveData<List<Story>>
        get() = _storyList

}