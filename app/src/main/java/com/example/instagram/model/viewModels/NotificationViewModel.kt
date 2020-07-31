package com.example.instagram.model.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.instagram.model.Notification


class NotificationViewModel  : ViewModel(){

    val _notificationsList =  MutableLiveData<List<Notification>>()
    val notificationsList : LiveData<List<Notification>>
    get() = _notificationsList

}