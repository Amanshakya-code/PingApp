package com.example.ping

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var phoneNumber:String
    private lateinit var countryCode:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //add hint request for the phone number // homework
        setContentView(R.layout.activity_login)

        phoneNumberEt.addTextChangedListener {
            nextBtn.isEnabled = !(it.isNullOrBlank() || it.length<10) // nextbtn only enable if the length is greater then 10 or it's not the blank edittext
        }
        nextBtn.setOnClickListener {
            checknumber()
        }
    }
    private fun checknumber(){
        countryCode = ccp.selectedCountryCodeWithPlus
        phoneNumber = countryCode+phoneNumberEt.text.toString()
        notifyuser()
    }
    private fun notifyuser(){
        MaterialAlertDialogBuilder(this).apply {
            setMessage("We will be verifying the phone number: $phoneNumber\n"+"Is this OK,or would you like to edit the number ?")
            setPositiveButton("Ok"){ _,_ -> // lamba fun with unnamed arugment
                showOtpActvity()
            }
            setNegativeButton("Edit"){dialog,which ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }
    private fun showOtpActvity(){
        startActivity(Intent(this,OtpActivity::class.java).putExtra(PHONE_NUMBER,phoneNumber))
        finish()
    }

}