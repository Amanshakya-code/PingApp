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
import com.example.ping.models.settingitem

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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