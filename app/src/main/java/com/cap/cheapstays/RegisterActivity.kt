package com.cap.cheapstays

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RegisterActivity : AppCompatActivity() {
    var mFullName: EditText? = null
    var mEmail: EditText? = null
    var mPassword: EditText? = null
    var confirmPassword: EditText? = null
    var mPhone: EditText? = null
    var mRegisterBtn: Button? = null
    var mLoginBtn: TextView? = null
    var fAuth: FirebaseAuth? = null
    var progressBarRegister: ProgressBar? = null
    var fStore: FirebaseFirestore? = null
    var userID: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mFullName = findViewById(R.id.username)
        mEmail = findViewById(R.id.register_email)
        mPassword = findViewById(R.id.register_password)
        mPhone = findViewById(R.id.mobile)
        mRegisterBtn = findViewById(R.id.signupbutton)
        mLoginBtn = findViewById(R.id.already_Have_an_account)
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        progressBarRegister = findViewById(R.id.progressBarRegister)
        confirmPassword=findViewById(R.id.register_confirm_password)

        if (fAuth!!.currentUser != null) {
            startActivity(Intent(applicationContext, HotelActivity::class.java))
            finish()
        }

        mLoginBtn?.setOnClickListener(View.OnClickListener { startActivity(Intent(applicationContext, LoginActivity::class.java)) })

        mRegisterBtn?.setOnClickListener(View.OnClickListener {

            if (TextUtils.isEmpty(mFullName?.text.toString())){
           mFullName?.error="Please Enter Name"
            }
            else if (TextUtils.isEmpty(mPhone?.text.toString())) {
              mPhone?.error="Please Enter Mobile Number"
            }
            else if (mPhone?.text.toString().length!=10){
           mPhone?.error="Please Enter a 10 Digit Mobile Number"
            }
            else if (TextUtils.isEmpty(mEmail?.text.toString())) {
             mEmail?.error="Pleas Enter Email Address"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail?.text.toString()).matches()) {
           mEmail?.error="Pleas Enter valid Email Address"
            }else if (TextUtils.isEmpty(mPassword?.text.toString())) {
           mPassword?.error="Pleas Enter Password"
            }
          else if (mPassword?.text.toString().length < 8) {
         mPassword?.error="Pleas Enter 8 or more numbers password"
            }
            else if (mPassword?.text.toString()!=confirmPassword?.text.toString()){
              confirmPassword?.error="Password must be Same"
            }
            else {
                progressBarRegister?.visibility = View.VISIBLE



            // register the user in firebase
            fAuth!!.createUserWithEmailAndPassword(
                mEmail!!.text.toString(),
                mPassword!!.text.toString()
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    // send verification link
                    val fuser = fAuth!!.currentUser
                    fuser!!.sendEmailVerification().addOnSuccessListener {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Verification Email Has been Sent.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener { e ->
                        Log.d(
                            TAG,
                            "onFailure: Email not sent " + e.message
                        )
                    }
                    Toast.makeText(this@RegisterActivity, "User Created.", Toast.LENGTH_SHORT)
                        .show()
                    userID = fAuth!!.currentUser!!.uid
                    val documentReference = fStore!!.collection("users").document(userID!!)
                    val user: MutableMap<String, Any> = HashMap()
                    user["username"] = mFullName!!.text.toString()
                    user["email"] = mEmail!!.text.toString()
                    user["mobile"] = mPhone!!.text.toString()
                    user["password"] = mPassword!!.text.toString()
                    documentReference.set(user).addOnSuccessListener {
                        Log.d(
                            TAG,
                            "onSuccess: user Profile is created for $userID"
                        )
                    }
                        .addOnFailureListener { e -> Log.d(TAG, "onFailure: $e") }
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Error ! " + task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBarRegister?.visibility = View.GONE
                }
            }
        }
        })

    }

    companion object {
        const val TAG = "TAG"
    }
}

