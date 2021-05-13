package com.example.ping

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_friend_profile_view.*
import java.lang.Exception

class FriendProfileView : AppCompatActivity() {
    private var name:String?= ""
    private var image:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile_view)
        name = intent.getStringExtra(NAME)
        image = intent.getStringExtra(IMAGE)

        friendprofilename.text = name
        Picasso.get().load(image).into(friendprofileimage, object : com.squareup.picasso.Callback {
            override fun onSuccess() {
                friendProfilePbar.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                friendProfilePbar.visibility = View.GONE
            }

        })
    }
} 