package com.example.ping

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import com.example.ping.adapter.settingAdapter
import com.example.ping.models.User
import com.example.ping.models.settingitem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profileview.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.lang.Exception

class Settings : AppCompatActivity() {

    lateinit var currentUserprofile: User
    private var mCurrentId:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        mCurrentId = FirebaseAuth.getInstance().uid!!
        FirebaseFirestore.getInstance().collection("users").document(mCurrentId!!).get().addOnSuccessListener {
            currentUserprofile = it.toObject(User::class.java)!!
            // Log.i("fault",currentUserprofile.toString())
            Picasso.get().load(currentUserprofile.imageUrl).into(settingUserImage,object: com.squareup.picasso.Callback{
                override fun onSuccess() {
                    settingPbar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    settingPbar.visibility = View.GONE
                }

            })
            settingUserName.text = currentUserprofile.name
        }

        var listView = findViewById<ListView>(R.id.listview)
        var list = mutableListOf<settingitem>()
        list.add(settingitem(R.drawable.profile,"Edit Profile"))
        list.add(settingitem(R.drawable.ic_baseline_notifications_active_24,"Notification status"))
        list.add(settingitem(R.drawable.privacy,"Privacy Rules"))
        list.add(settingitem(R.drawable.ic_baseline_eco_24,"About Us"))
        list.add(settingitem(R.drawable.ic_baseline_logout_24,"Logout"))

        listView.adapter = settingAdapter(this,list)
        listView.setOnItemClickListener { parent:AdapterView<*>, view: View, position:Int, id:Long ->
            if(position == 0){
                startActivity(Intent(this,profileview::class.java))
            }
            if(position == 1){
                Toast.makeText(this,"this is notification",Toast.LENGTH_SHORT).show()
            }
            if(position == 2){
                Toast.makeText(this,"this is privacy",Toast.LENGTH_SHORT).show()
            }
            if(position == 3){
                Toast.makeText(this,"this is about us",Toast.LENGTH_SHORT).show()
            }
            if(position == 4){
                var dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle("Thanks For using the App")
                dialogBuilder.setMessage("will see you later :)")
                    .setCancelable(true)
                    .setPositiveButton("Logout",DialogInterface.OnClickListener(){
                            dialog, which -> dialog.dismiss()
                    })
                    .setNegativeButton("cancel",DialogInterface.OnClickListener(){
                            dialog, which -> dialog.dismiss()
                    })
                val alert = dialogBuilder.create()
                alert.show()
            }
        }
    }
}