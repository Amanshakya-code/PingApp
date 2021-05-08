package com.example.ping.network

import com.example.ping.network.Constants.Companion.CONTENT_TYPE
import com.example.ping.network.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body

import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Content-Type:$CONTENT_TYPE","Authorization:key=$SERVER_KEY")
    @POST("send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}