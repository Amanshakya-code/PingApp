package com.example.ping

import android.content.Context
import java.util.*

interface  ChatEvent {
    val sendAt:Date
    val combineId:String
    val msgId:String
}
data class Message(
        val msg:String,
        val senderId:String,
        override val msgId:String,
        override val combineId:String,
        val type:String = "TEXT",
        val status:Int = 1,

        override val sendAt: Date = Date()
):ChatEvent{
    constructor():this("","","","","",1,Date())
}

data class DateHeader(
    override val sendAt: Date = Date(),
    val context: Context, override val combineId: String, override val msgId: String
):ChatEvent{
    val date:String = sendAt.formatAsHeader(context)
}
