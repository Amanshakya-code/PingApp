package com.example.ping.models

import com.google.firebase.firestore.FieldValue
import java.lang.reflect.Field

data class User(
    val name:String,
    val imageUrl:String,
    val thumbImage:String,
    val uid:String,
    val deviceToken:String,
    val status:String,
    var onlineStatus: String
        // when we make a data class for the firebase we always make empty constructor
){
    constructor():this("","","","","","", "")
    constructor(name: String,imageUrl: String,thumbImage: String,uid: String,deviceToken: String,onlineStatus: String):this(name,imageUrl,thumbImage,uid,deviceToken,"Hey there i am using Ping",onlineStatus)
}
