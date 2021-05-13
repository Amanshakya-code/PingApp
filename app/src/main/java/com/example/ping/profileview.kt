package com.example.ping

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.ping.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profileview.*
import java.lang.Exception

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
            Picasso.get().load(currentUserprofile.imageUrl).into(profileimage,object: com.squareup.picasso.Callback{
                override fun onSuccess() {
                    profileProgressbar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    profileProgressbar.visibility = View.GONE
                }

            })
            profilename.text = currentUserprofile.name
        }
        editprofile.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }

    }
}