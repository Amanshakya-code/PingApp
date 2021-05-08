package com.example.ping

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ping.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profileview.*

class profileview : AppCompatActivity() {

    lateinit var currentUserprofile: User
    private var mCurrentId:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profileview)
        mCurrentId = FirebaseAuth.getInstance().uid!!
        FirebaseFirestore.getInstance().collection("users").document(mCurrentId!!).get().addOnSuccessListener {
            currentUserprofile = it.toObject(User::class.java)!!
           // Log.i("fault",currentUserprofile.toString())
            Picasso.get().load(currentUserprofile.imageUrl).into(profileimage)
            profilename.text = currentUserprofile.name
        }
        editprofile.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }

    }
}