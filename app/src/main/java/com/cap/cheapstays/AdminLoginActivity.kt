package com.cap.cheapstays

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
        val docRef = db.collection(getString(R.string.admin_path)).document(getString(R.string.admin_doc_path))

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                     firebaseEmail= document.getString(getString(R.string.adminEmail)).toString()
                     firebasePassword=document.getString(getString(R.string.adminPassword)).toString()
                } else {

                    Toast.makeText(this,getString(R.string.document_not_exist),Toast.LENGTH_SHORT).show()
                }
            }
        fAuth = FirebaseAuth.getInstance()

        login?.setOnClickListener(View.OnClickListener {
            val adminMail = adminEmail?.text.toString().trim{ it <= ' ' }
            val adminPass = adminPassword?.text.toString().trim{ it <= ' ' }

            if (TextUtils.isEmpty(adminMail)) {
           adminEmail?.error=getString(R.string.enterEmail)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(adminMail).matches()) {
            adminEmail?.error=getString(R.string.PleaseEntervalidEmailAddress)
            } else if (TextUtils.isEmpty(adminPass)) {
               adminPassword?.error=getString(R.string.enterPassword)
            }  else {
                progressBarAdmin.visibility= View.VISIBLE

                fAuth?.signInWithEmailAndPassword(firebaseEmail!!,firebasePassword!!)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, getString(R.string.toast_loged_in), Toast.LENGTH_SHORT)
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