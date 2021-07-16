package com.example.ping

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.ping.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_friend_profile_view.*
import java.lang.Exception
import java.util.*

class FriendProfileView : AppCompatActivity() {
    private var name:String?= ""
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile_view)
        name = intent.getStringExtra(NAME)
        FirebaseFirestore.getInstance().collection("users").document(name!!).get().addOnSuccessListener {
            user = it.toObject(User::class.java)!!
            friendname.text = user.name
            friendstatus.text = user.status
            if(user.imageUrl.isEmpty())
            {
                friendphototxt.visibility = View.VISIBLE
                friendphototxt.text = user.name[0].toString().toUpperCase(Locale.ROOT)
            }
            else{
                Picasso.get()
                    .load(user.imageUrl)
                    .placeholder(R.drawable.defaultavatar)
                    .error(R.drawable.defaultavatar)
                    .into(friendprofilephoto)
            }
        }
        reportbtn.setOnClickListener {
            var dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Report User :/")
                .setCancelable(true)
                .setPositiveButton("Report", DialogInterface.OnClickListener(){
                        dialog, which -> dialog.dismiss()
                    Toast.makeText(this,"Reported!!",Toast.LENGTH_SHORT).show()
                })
                .setNegativeButton("cancel", DialogInterface.OnClickListener(){
                        dialog, which -> dialog.dismiss()
                })
            val alert = dialogBuilder.create()
            alert.show()
        }
    }
} 