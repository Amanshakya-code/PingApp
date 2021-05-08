package com.example.ping

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_friend_profile_view.*

class FriendProfileView : AppCompatActivity() {
    private var name:String?= ""
    private var image:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile_view)
        name = intent.getStringExtra(NAME)
        image = intent.getStringExtra(IMAGE)

        friendprofilename.text = name
        Picasso.get().load(image).into(friendprofileimage)
    }
}