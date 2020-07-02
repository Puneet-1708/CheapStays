package com.cap.cheapstays

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.layout_admin_login.*


class AdminLoginActivity:AppCompatActivity() {

    var fAuth: FirebaseAuth? = null
    var firebaseEmail:String?=null
  var firebasePassword:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)


        val adminEmail: EditText? = findViewById(R.id.admin_login_email)
        val adminPassword: EditText? = findViewById(R.id.admin_login_password)

        val db=FirebaseFirestore.getInstance()
        val docRef = db.collection("admin").document("Puneet")

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.e("exist","${document.data}")
                     firebaseEmail= document.getString("adminEmail").toString()
                     firebasePassword=document.getString("adminPassword").toString()
                } else {

                    Log.e("noexist","No data")
                    Toast.makeText(this,"Document does not exist!",Toast.LENGTH_SHORT).show()
                }
            }
        fAuth = FirebaseAuth.getInstance()

        login?.setOnClickListener(View.OnClickListener {
            val adminMail = adminEmail?.text.toString().trim{ it <= ' ' }
            val adminPass = adminPassword?.text.toString().trim{ it <= ' ' }

            if (TextUtils.isEmpty(adminMail)) {
           adminEmail?.error="Please Enter Email"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(adminMail).matches()) {
            adminEmail?.error="Pleas Enter valid Email Address"
            } else if (TextUtils.isEmpty(adminPass)) {
               adminPassword?.error="Pleas Enter Password"
            }  else {
                progressBarAdmin.visibility= View.VISIBLE

                fAuth?.signInWithEmailAndPassword(firebaseEmail!!,firebasePassword!!)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(Intent(this, HotelAdminActivity::class.java))
                        } else {
                            Toast.makeText(
                                this,
                                "Error ! " + task.exception!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            progressBarAdmin?.visibility = View.GONE
                        }
                    }
            }
        })
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}