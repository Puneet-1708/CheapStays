package com.cap.cheapstays

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    var fullName: TextView? = null
    var email: TextView? = null
    var phone: TextView? = null
    var verifyMsg: TextView? = null
    var fAuth: FirebaseAuth? = null
    var fStore: FirebaseFirestore? = null
    var userId: String? = null
    var resendCode: Button? = null
    var resetPassLocal: Button? = null
    var changeProfileImage: Button? = null
    var user: FirebaseUser? = null
    var profileImage: ImageView? = null
    var storageReference: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_profile)
        phone = findViewById(R.id.profilePhone)
        fullName = findViewById(R.id.profileName)
        email = findViewById(R.id.profileEmail)
        resetPassLocal = findViewById(R.id.resetPasswordLocal)
        profileImage = findViewById(R.id.profileImage)
        changeProfileImage = findViewById(R.id.changeProfile)
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        val profileRef = storageReference!!.child("users/" + fAuth!!.currentUser!!.uid + "/profile.jpg")
        profileRef.downloadUrl.addOnSuccessListener { uri -> Picasso.get().load(uri).into(profileImage) }
        resendCode = findViewById(R.id.resendCode)
        verifyMsg = findViewById(R.id.verifyMsg)
        userId = fAuth!!.currentUser!!.uid
        user = fAuth!!.currentUser
        if (!user!!.isEmailVerified) {
            verifyMsg?.visibility = View.VISIBLE
            resendCode?.visibility = View.VISIBLE
            resendCode?.setOnClickListener(View.OnClickListener { v -> user!!.sendEmailVerification().addOnSuccessListener { Toast.makeText(v.context, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show() }.addOnFailureListener { e -> Log.d("tag", "onFailure: Email not sent " + e.message) } })
        }
        val documentReference = fStore!!.collection("users").document(userId!!)
        documentReference.addSnapshotListener(this) { documentSnapshot, e ->
            if (documentSnapshot!!.exists()) {
                phone?.text = documentSnapshot.getString("mobile")
                fullName?.text = documentSnapshot.getString("username")
                email?.text = documentSnapshot.getString("email")
            } else {
                Log.d("tag", "onEvent: Document do not exists")
            }
        }
        resetPassLocal?.setOnClickListener(View.OnClickListener { v ->
            val resetPassword = EditText(v.context)
            val passwordResetDialog = AlertDialog.Builder(v.context)
            passwordResetDialog.setTitle("Reset Password ?")
            passwordResetDialog.setMessage("Enter New Password > 6 Characters long.")
            passwordResetDialog.setView(resetPassword)
            passwordResetDialog.setPositiveButton("Yes") { dialog, which -> // extract the email and send reset link
                val newPassword = resetPassword.text.toString()
                user!!.updatePassword(newPassword).addOnSuccessListener { Toast.makeText(this@ProfileActivity, "Password Reset Successfully.", Toast.LENGTH_SHORT).show() }.addOnFailureListener { Toast.makeText(this@ProfileActivity, "Password Reset Failed.", Toast.LENGTH_SHORT).show() }
            }
            passwordResetDialog.setNegativeButton("No") { dialog, which ->
                // close
            }
            passwordResetDialog.create().show()
        })
        changeProfileImage?.setOnClickListener(View.OnClickListener { v ->
            // open gallery
            val i = Intent(v.context, EditProfile::class.java)
            i.putExtra("fullName", fullName?.text.toString())
            i.putExtra("email", email?.text.toString())
            i.putExtra("phone", phone?.text.toString())
            startActivity(i)
            //
        })
    }

    fun logout(view: View?) {
        FirebaseAuth.getInstance().signOut() //logout
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }

    companion object {
        private const val GALLERY_INTENT_CODE = 1023
    }
}