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
           mFullName?.error=getString(R.string.PleaseEnterName)
            }
            else if (TextUtils.isEmpty(mPhone?.text.toString())) {
              mPhone?.error=getString(R.string.PleaseEnterMobileNumber)
            }
            else if (mPhone?.text.toString().length!=10){
           mPhone?.error=getString(R.string.EnterMobileNumber)
            }
            else if (TextUtils.isEmpty(mEmail?.text.toString())) {
             mEmail?.error=getString(R.string.PleaseEnterEmailAddress)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail?.text.toString()).matches()) {
           mEmail?.error=getString(R.string.PleaseEntervalidEmailAddress)
            }else if (TextUtils.isEmpty(mPassword?.text.toString())) {
           mPassword?.error=getString(R.string.PleaseEnterPassword)
            }
          else if (mPassword?.text.toString().length < 8) {
         mPassword?.error=getString(R.string.PleaseEnter8Password)
            }
            else if (mPassword?.text.toString()!=confirmPassword?.text.toString()){
              confirmPassword?.error=getString(R.string.PasswordmustbeSame)
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
                            getString(R.string.ToastVerificationEmail),
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener { e ->
                      Toast.makeText(this,getString(R.string.Emailnotsent),Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(this@RegisterActivity, getString(R.string.UserCreated), Toast.LENGTH_SHORT)
                        .show()
                    userID = fAuth!!.currentUser!!.uid
                    val documentReference = fStore!!.collection(getString(R.string.users)).document(userID!!)
                    val user: MutableMap<String, Any> = HashMap()
                    user[getString(R.string.username)] = mFullName!!.text.toString()
                    user[getString(R.string.r_email)] = mEmail!!.text.toString()
                    user[getString(R.string.r_mobile)] = mPhone!!.text.toString()
                    user[getString(R.string.r_password)] = mPassword!!.text.toString()
                    documentReference.set(user).addOnSuccessListener {
                        Toast.makeText(this,getString(R.string.ToastProfileCreated),Toast.LENGTH_SHORT).show()

                    }
                        .addOnFailureListener {
                            Toast.makeText(this,getString(R.string.ToastProfileNotCreated),Toast.LENGTH_SHORT).show()
                        }
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

}

