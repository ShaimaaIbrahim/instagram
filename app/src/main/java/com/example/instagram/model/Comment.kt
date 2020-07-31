package com.example.instagram.model

import java.util.concurrent.Flow

class Comment() {

    var comment : String =""
    var publisher : String =""

    constructor(comment: String , publisher: String) : this(){
        this.comment=comment
        this.publisher=publisher
    }

}