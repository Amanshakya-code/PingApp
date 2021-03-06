package com.example.ping

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit
import kotlin.math.log

const val PHONE_NUMBER = "phoneNumber"

class OtpActivity : AppCompatActivity(),View.OnClickListener{
    var phoneNumber:String? = null
    var mVerificationId:String? = null
    lateinit var auth: FirebaseAuth
    private var mcounterTimer: CountDownTimer?=null
    var mResendToken:PhoneAuthProvider.ForceResendingToken?=null
    private lateinit var progressDialog:ProgressDialog
    lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        auth = FirebaseAuth.getInstance()

        initview()
        startverify()
    }

    private fun startverify() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
     
        showTimer(60000)
        progressDialog = createProgressDialog("Sending a Verification Code",false)
        progressDialog.show()

    }
    private fun showTimer(millisecInFuture:Long){
        resendBtn.isEnabled = false
        mcounterTimer = object:CountDownTimer(millisecInFuture,1000){
            override fun onTick(millisUntilFinished: Long) {
                counterTv.isVisible = true
                counterTv.text = getString(R.string.second_remaining,millisUntilFinished/1000)
            }

            override fun onFinish() {
                resendBtn.isEnabled = true
                counterTv.isVisible = false
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mcounterTimer !=null){
            mcounterTimer!!.cancel()
        }
    }


    private fun initview(){
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        verifyTv.text = getString(R.string.verify_number,phoneNumber)
        setSpannableString()
        verificationBtn.setOnClickListener(this)
        resendBtn.setOnClickListener(this)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                if(::progressDialog.isInitialized){
                    progressDialog.dismiss()
                }
                val smsmessage = credential.smsCode
                if(!smsmessage.isNullOrBlank())
                    sentcodeEt.setText(smsmessage)
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                if(::progressDialog.isInitialized){
                    progressDialog.dismiss()
                }
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.i("Exception:", "FirebaseAuthInvalidCredentialsException", e)
                    Log.i("=========:", "FirebaseAuthInvalidCredentialsException " + e.message)
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.i("Exception:", "FirebaseTooManyRequestsException", e)
                }
                Log.i("error",e.localizedMessage)
                notifyUserAndRetry("Your Phone Number might be wrong or connection error Retry again!")
                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                progressDialog.dismiss()
                counterTv.isVisible = false
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Log.i("lol","ready to go ")
                    if (::progressDialog.isInitialized) {
                        progressDialog.dismiss()
                    }
                    startActivity(Intent(this,SignUpActivity::class.java))
                    finish()

                }
                else
                {
                    notifyUserAndRetry("Your Phone Number Verification Failed Try Again !!")
                }
            }

    }

    private fun notifyUserAndRetry(message: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("Ok") { _,_ ->
                showLoginActivity()
            }

            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            setCancelable(false)
            create()
            show()
        }
    }
    private fun setSpannableString() {
        val span = SpannableString(getString(R.string.waiting_text,phoneNumber))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // send back to the login activity
                showLoginActivity()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ds.linkColor
            }
        }
        span.setSpan(clickableSpan,span.length-14,span.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // this will make wrong number as a link clickable
        waitingTv.movementMethod = LinkMovementMethod.getInstance()
        waitingTv.text = span
    }
    private fun showLoginActivity(){
        startActivity(Intent(this,LoginActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }
    override fun onBackPressed() {
        // this will disable the back function
    }

    override fun onClick(v: View?) {
        when(v){
            verificationBtn->{
                val code = sentcodeEt.text.toString()
                if(code.isNotEmpty()&& !mVerificationId.isNullOrBlank()) {
                    progressDialog = createProgressDialog("please wait....",false)
                    progressDialog.show()
                    val credential = PhoneAuthProvider.getCredential(mVerificationId!!,code)
                    signInWithPhoneAuthCredential(credential)
                }
            }
            resendBtn->{
                val code = sentcodeEt.text.toString()
                if(mResendToken!=null) {
                    showTimer(60000)
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(mResendToken)
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                    progressDialog = createProgressDialog("Sending Verification Code ",false)
                    progressDialog.show()
                }
            }
        }
    }

}
fun Context.createProgressDialog(message:String, isCancelable:Boolean):ProgressDialog{
    return ProgressDialog(this).apply {
        setCancelable(false)
        setMessage(message)
        setCanceledOnTouchOutside(false)
    }
}