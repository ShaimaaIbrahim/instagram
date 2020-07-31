package com.example.instagram.model.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.instagram.model.Post

class MonawatViewModel : ViewModel(){


    val _monawatList =  MutableLiveData<List<String>>()
    val monawatList : LiveData<List<String>>
        get() = _monawatList


}