package com.example.ping

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast

import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.ping.models.User

import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    val storage by lazy {
        FirebaseStorage.getInstance()
    }
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance()
    }
    lateinit var downloadURL: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        downloadURL = ""
        changesignupimage.setOnClickListener {
            checkPermissionForImage()
        }
        nextBtn.setOnClickListener {
            val name = nameedit.text.toString()
            val status = status.text.toString()
            if (name.isEmpty())
            {
                Toast.makeText(this,"Name cannot be empty!!",Toast.LENGTH_SHORT).show()
            }
            else
            {
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@OnCompleteListener
                    }
                    // Get new FCM registration token
                    var token = task.result.toString()

                    val user = User(name,downloadURL,downloadURL,auth.uid!!,token,status,"online")
                    database.collection("users").document(auth.uid!!).set(user).addOnSuccessListener {
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }.addOnFailureListener{
                        nextBtn.isEnabled = true
                    }
                    Log.i("token",token)
                })
            }
        }
        deleteImageurl.setOnClickListener {
            downloadURL = ""
            signupprofile.setImageResource(R.drawable.defaultavatar)
            deleteImageurl.visibility = View.GONE
        }
    }

    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionWrite = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                requestPermissions(
                    permission,
                    1001
                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_READ LIKE 1001
                requestPermissions(
                    permissionWrite,
                    1002
                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_WRITE LIKE 1002
            } else {
                pickImageFromGallery()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(
            intent,1000
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 1000)
        {
            //this will store the path for the image
            data?.data?.let{
                signupprofile.setImageURI(it)
                uploadImage(it)
            }
        }
        if(requestCode == 234)
        {
            Log.i("image", "run")

        }
    }

    private fun uploadImage(it: Uri) {
        deleteImageurl.visibility = View.VISIBLE
        Toast.makeText(this,"Please wait your data is uploading",Toast.LENGTH_LONG).show()
        showbar()
        nextBtn.isEnabled = false // no intererruption in uploading the image
        val ref = storage.reference.child("uploads/"+auth.uid.toString())
        val uploadTask = ref.putFile(it)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>>{ task->
            if (!task.isSuccessful)
            {
                task.exception.let {
                    throw it!!
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener {task->
            Toast.makeText(this,"There your go :)",Toast.LENGTH_SHORT).show()
            hidebar()
            nextBtn.isEnabled = true
            if (task.isSuccessful)
            {
                downloadURL = task.result.toString()
            }
            else
            {

            }
        }.addOnFailureListener{
            deleteImageurl.visibility = View.GONE
            hidebar()
            Toast.makeText(this,"Not able to upload the image",Toast.LENGTH_SHORT).show()
        }
    }
    private fun showbar(){
        signupbackload.visibility = View.VISIBLE
        signuploadingbar.visibility = View.VISIBLE
    }
    private fun hidebar(){
        signupbackload.visibility = View.GONE
        signuploadingbar.visibility = View.GONE
    }
}