package com.cap.cheapstays


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    var mEmail: EditText? = null
    var mPassword: EditText? = null
    var mLoginBtn: Button? = null
    var adminLoginBtn: Button? = null
    var mCreateBtn: TextView? = null
    var forgotTextLink: TextView? = null
    var progressBar: ProgressBar? = null
    var fAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mEmail = findViewById(R.id.login_email)
        mPassword = findViewById(R.id.login_password)
        progressBar = findViewById(R.id.progressBar)
        fAuth = FirebaseAuth.getInstance()
        mLoginBtn = findViewById(R.id.login_button)
        mCreateBtn = findViewById(R.id.need_account)
        adminLoginBtn=findViewById(R.id.adminLogin)
        forgotTextLink = findViewById(R.id.forget_password)

        mLoginBtn?.setOnClickListener(View.OnClickListener {
            val email = mEmail?.text.toString().trim { it <= ' ' }
            val password = mPassword?.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                mEmail?.error = "Email is Required."
                return@OnClickListener
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(
                    this@LoginActivity,
                    "Pleas Enter valid Email Address",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if (TextUtils.isEmpty(password)) {
                mPassword?.error = "Password is Required."
                return@OnClickListener
            }

            else{

            progressBar?.visibility = View.VISIBLE


            fAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Logged in Successfully", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(applicationContext,MapsActivity::class.java))
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error ! " + task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar?.visibility = View.GONE
                }
            }

            }
        })
        mCreateBtn?.setOnClickListener(View.OnClickListener { startActivity(Intent(applicationContext, RegisterActivity::class.java)) })
        adminLoginBtn?.setOnClickListener {
            startActivity(Intent(this,AdminLoginActivity::class.java))
        }

        forgotTextLink?.setOnClickListener(View.OnClickListener { v ->
            val resetMail = EditText(v.context)
            val passwordResetDialog = AlertDialog.Builder(v.context)
            passwordResetDialog.setTitle("Reset Password ?")
            passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.")
            passwordResetDialog.setView(resetMail)
            passwordResetDialog.setPositiveButton("Yes") { dialog, which -> // extract the email and send reset link
                val mail = resetMail.text.toString()
                fAuth!!.sendPasswordResetEmail(mail).addOnSuccessListener { Toast.makeText(this@LoginActivity, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show() }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@LoginActivity, "Error ! Reset Link is Not Sent" + e.message, Toast.LENGTH_SHORT).show() }
            }
            passwordResetDialog.setNegativeButton("No") { dialog, which ->
            }
            passwordResetDialog.create().show()
        })
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure want to quit?")
        builder.setPositiveButton("Yes", { dialogInterface: DialogInterface, i: Int -> finishAffinity() })
        builder.setNegativeButton("No",{ dialogInterface: DialogInterface, i: Int -> })
        builder.show()
    }
}