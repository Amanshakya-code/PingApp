package com.example.ping

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import com.example.ping.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.list_item.view.*

class EditProfile : AppCompatActivity() {
    private var mCurrentId:String?=""
    val database by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        mCurrentId = FirebaseAuth.getInstance().uid!!
        window.statusBarColor = resources.getColor(R.color.white, theme)
        val user:User = intent.getSerializableExtra("currentuser") as User
        Editname.setText(user.name)
        editstatus.setText(user.status)
        if(user.imageUrl.isEmpty())
        {
            profilePhoto.setImageResource(R.drawable.defaultavatar)
        }
        else{
            Picasso.get()
                .load(user.imageUrl)
                .placeholder(R.drawable.defaultavatar)
                .error(R.drawable.defaultavatar)
                .into(profilePhoto)
        }


        donebtn.setOnClickListener {
            if(Editname.text.isEmpty())
            {
                Toast.makeText(this,"Name Field cannot be empty",Toast.LENGTH_SHORT).show()
            }
            else
            {
                val ref  = database.collection("users").document(mCurrentId!!)
                var hp: HashMap<String, Any> = HashMap<String, Any>()
                if(editstatus.text.isEmpty())
                {
                    hp.put("name",Editname.text.toString())
                    hp.put("status","")
                    ref.update(hp)
                }
                else{
                    hp.put("name",Editname.text.toString())
                    hp.put("status",editstatus.text.toString())
                    ref.update(hp)
                    finish()
                }
            }

        }

    }
}