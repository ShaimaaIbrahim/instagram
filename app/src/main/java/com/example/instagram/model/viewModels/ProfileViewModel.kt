package com.example.instagram.model.viewModels

import android.view.View
import android.view.ViewManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.instagram.model.Grid
import com.example.instagram.model.Post

class ProfileViewModel : ViewModel(){

    val _imagesList =  MutableLiveData<List<Post>>()
    val imagesList : LiveData<List<Post>>
        get() = _imagesList

    val _imageSavedList =  MutableLiveData<List<Post>>()
    val imageSavedList : LiveData<List<Post>>
        get() = _imageSavedList

}