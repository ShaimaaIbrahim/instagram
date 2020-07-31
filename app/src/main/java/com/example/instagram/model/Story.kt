package com.example.instagram.model

class Story {

    private var imageUrl : String= ""
    private var timeStar : Long = 0
    private var timeEnd : Long = 0
    private var storyId : String= ""
    private var userId : String =""

    constructor()

    constructor(imageUrl: String, timeStar: Long, timeEnd: Long, storyId: String, userId: String) {
        this.imageUrl = imageUrl
        this.timeStar = timeStar
        this.timeEnd = timeEnd
        this.storyId = storyId
        this.userId = userId
    }

    fun getImageUrl(): String? {
        return imageUrl
    }

    fun setImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    fun getTimeStar(): Long? {
        return timeStar
    }

    fun setTimeStar(timeStar: Long) {
        this.timeStar = timeStar
    }

    fun getTimeEnd(): Long? {
        return timeEnd
    }

    fun setTimeEnd(timeEnd: Long) {
        this.timeEnd = timeEnd
    }

    fun getStoryId(): String? {
        return storyId
    }

    fun setStoryId(storyId: String) {
        this.storyId = storyId
    }

    fun getUserId(): String? {
        return userId
    }

    fun setUserId(userId: String) {
        this.userId = userId
    }
}