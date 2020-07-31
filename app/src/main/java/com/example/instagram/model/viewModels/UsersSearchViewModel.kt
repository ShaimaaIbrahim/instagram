package com.example.instagram.model.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.instagram.model.User

class UsersSearchViewModel : ViewModel(){

    public lateinit var  usersList : ArrayList<User>

     val _searchUserList = MutableLiveData<List<User>>()
     val searchUserList : LiveData<List<User>>
     get() =_searchUserList


  init {
   /*   usersList = ArrayList()
     _searchUserList.value = usersList*/

  }

}