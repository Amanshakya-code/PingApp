package com.example.ping.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import com.example.ping.EditProfile
import com.example.ping.LoginActivity
import com.example.ping.MainActivity
import com.example.ping.R
import com.example.ping.adapter.settingAdapter
import com.example.ping.models.User
import com.example.ping.models.settingitem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import java.util.*


class UserProfile : Fragment(R.layout.fragment_user_profile) {
    lateinit var currentUserprofile: User
    private var mCurrentId:String?=""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCurrentId = FirebaseAuth.getInstance().uid!!
        FirebaseFirestore.getInstance().collection("users").document(mCurrentId!!).get().addOnSuccessListener {
            currentUserprofile = it.toObject(User::class.java)!!
            // Log.i("fault",currentUserprofile.toString())
            if(currentUserprofile.imageUrl == "")
            {
                usertextname.visibility = View.VISIBLE
                usertextname.text = currentUserprofile.name[0].toString().toUpperCase(Locale.ROOT)
            }
            else {
                usertextname.visibility = View.GONE
                Picasso.get()
                    .load(currentUserprofile.imageUrl)
                    .placeholder(R.drawable.defaultavatar)
                    .into(myprofilephoto)
            }
            nametv.text = currentUserprofile.name
            status.text = currentUserprofile.status
        }
        FirebaseFirestore.getInstance().collection("users").document(mCurrentId!!).addSnapshotListener { value, error ->
            error?.let {
                Log.i("eoo","$it")
                return@addSnapshotListener
            }
            value?.let {
                if(it.exists()){
                    val name = it.getString("name")
                    val statustext = it.getString("status")
                    val image = it.getString("imageUrl")
                    if(image == "")
                    {
                        usertextname.visibility = View.VISIBLE
                        usertextname.text = name?.get(0)?.toString()!!.toUpperCase(Locale.ROOT)
                    }
                    else {
                        usertextname.visibility = View.GONE
                        Picasso.get()
                            .load(image)
                            .placeholder(R.drawable.defaultavatar)
                            .into(myprofilephoto)
                    }
                    nametv.text = name
                    status.text = statustext
                }
            }

        }
      //  var listView = findViewById<ListView>(R.id.settingListView)
        var list = mutableListOf<settingitem>()
        list.add(settingitem(R.drawable.profile,"Edit Profile","Change Image, Name and Status"))
        list.add(settingitem(R.drawable.ic_baseline_notifications_active_24,"Notification status","Turn on & off"))
        list.add(settingitem(R.drawable.privacy,"Privacy Rules","Terms and Privacy Policy"))
        list.add(settingitem(R.drawable.ic_baseline_eco_24,"About Us","Know about developer"))
        list.add(settingitem(R.drawable.ic_baseline_logout_24,"Logout","Sign in with other Account"))


        settingListView.adapter = settingAdapter((activity as MainActivity),list)
        settingListView.setOnItemClickListener { parent: AdapterView<*>, view: View, position:Int, id:Long ->
            if(position == 0){
                val intent = Intent(requireContext(),EditProfile::class.java)
                intent.putExtra("currentuser",currentUserprofile)
                startActivity(intent)
            }
            if(position == 1){
               // Toast.makeText(this,"Notification", Toast.LENGTH_SHORT).show()
            }
            if(position == 2){
                //Toast.makeText(this,"privacy rules", Toast.LENGTH_SHORT).show()
            }
            if(position == 3){
                val inflater = LayoutInflater.from(requireContext())
                val view = inflater.inflate(R.layout.alert_dialog,null)
                val alertDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setView(view)
                    .setCancelable(true)
                    .setPositiveButton("Thanks for using this App"){dialogInterface,which->
                    }
                    .create()
                alertDialog.show()
            }
            if(position == 4){
                var dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setTitle("Thanks For using the App")
                dialogBuilder.setMessage("will see you later :)")
                    .setCancelable(true)
                    .setPositiveButton("Logout", DialogInterface.OnClickListener(){
                            dialog, which -> dialog.dismiss()
                        val intent = Intent(requireContext(),LoginActivity::class.java)
                        startActivity(intent)
                        (activity as MainActivity).finish()
                    })
                    .setNegativeButton("cancel", DialogInterface.OnClickListener(){
                            dialog, which -> dialog.dismiss()
                    })
                val alert = dialogBuilder.create()
                alert.show()
            }
        }

    }
}