package com.example.ping

import android.app.Activity
import android.content.Intent
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.ping.models.User
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.*
import kotlin.collections.HashMap

class EditProfile : AppCompatActivity() {
    private var mCurrentId:String?=""
    val database by lazy {
        FirebaseFirestore.getInstance()
    }
    val storage by lazy {
        FirebaseStorage.getInstance()
    }
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var downloadURL:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        mCurrentId = auth.uid!!
       // window.statusBarColor = resources.getColor(R.color.white, theme)
        val user:User = intent.getSerializableExtra("currentuser") as User
        downloadURL = user.imageUrl
        Editname.setText(user.name)
        editstatus.setText(user.status)
        if(user.imageUrl.isEmpty())
        {
            profilenametext.visibility = View.VISIBLE
            profilenametext.text = user.name[0].toString().toUpperCase(Locale.ROOT)
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
                    hp.put("imageUrl",downloadURL)
                    hp.put("thumbImage",downloadURL)
                    ref.update(hp)
                    finish()
                }
                else{
                    hp.put("name",Editname.text.toString())
                    hp.put("status",editstatus.text.toString())
                    hp.put("imageUrl",downloadURL)
                    hp.put("thumbImage",downloadURL)
                    ref.update(hp)
                    finish()
                }
            }

        }

        changeImage.setOnClickListener {
            ImagePicker.with(this)
                .galleryMimeTypes(  //Exclude gif images
                    mimeTypes = arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    )
                )
                .compress(1024)
                .cropSquare() //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
        deleteImage.setOnClickListener {
            profilePhoto.setImageResource(R.color.purple_200)
            profilenametext.visibility = View.VISIBLE
            profilenametext.text = user.name[0].toString().toUpperCase(Locale.ROOT)
            downloadURL = ""
            deleteImage.visibility = View.GONE
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            Log.i("hituri", "$uri")
            uploadImage(uri)
        }
    }

    private fun uploadImage(uri: Uri) {
        profilenametext.visibility = View.GONE
        showloading()
        Picasso.get()
            .load(uri)
            .placeholder(R.drawable.defaultavatar)
            .error(R.drawable.defaultavatar)
            .into(profilePhoto)
        deleteImage.visibility = View.VISIBLE
        donebtn.isEnabled = false
        val ref = storage.reference.child("uploads/"+auth.uid.toString())
        val uploadTask = ref.putFile(uri)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task->
            if (!task.isSuccessful)
            {
                task.exception.let {
                    throw it!!
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task->
            deleteImage.visibility = View.VISIBLE
            donebtn.isEnabled = true
            hideloading()
            Toast.makeText(this,"There your go :)",Toast.LENGTH_SHORT).show()
            if (task.isSuccessful)
            {
                downloadURL = task.result.toString()
            }
            else
            {

            }
        }.addOnFailureListener{
            hideloading()
            Toast.makeText(this,"Not able to upload the image",Toast.LENGTH_SHORT).show()
        }

    }
    fun showloading(){
        loadblacksaved.visibility = View.VISIBLE
        savedprogressbar.visibility = View.VISIBLE
    }
    fun hideloading(){
        loadblacksaved.visibility = View.GONE
        savedprogressbar.visibility = View.GONE
    }

}