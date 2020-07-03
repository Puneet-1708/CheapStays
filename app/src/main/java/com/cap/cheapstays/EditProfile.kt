package com.cap.cheapstays

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.*

class EditProfile : AppCompatActivity() {
    var profileFullName: EditText? = null
    var profileEmail: EditText? = null
    var profilePhone: EditText? = null
    var profileImageView: ImageView? = null
    var saveBtn: Button? = null
    var fAuth: FirebaseAuth? = null
    var fStore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var storageReference: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        val data = intent
        val fullName = data.getStringExtra("fullName")
        val email = data.getStringExtra("email")
        val phone = data.getStringExtra("phone")
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        user = fAuth!!.currentUser
        storageReference = FirebaseStorage.getInstance().reference
        profileFullName = findViewById(R.id.profileFullName)
        profileEmail = findViewById(R.id.profileEmailAddress)
        profilePhone = findViewById(R.id.profilePhoneNo)
        profileImageView = findViewById(R.id.profileImageView)
        saveBtn = findViewById(R.id.saveProfileInfo)
        val profileRef = storageReference!!.child("users/" + fAuth!!.currentUser!!.uid + "/profile.jpg")
        profileRef.downloadUrl.addOnSuccessListener { uri -> Picasso.get().load(uri).into(profileImageView) }


        profileImageView?.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 1000)
        })


        saveBtn?.setOnClickListener(View.OnClickListener {
            if (profileFullName?.getText().toString().isEmpty() || profileEmail?.getText().toString().isEmpty() || profilePhone?.getText().toString().isEmpty()) {
                Toast.makeText(this@EditProfile, "One or Many fields are empty.", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val email = profileEmail?.getText().toString()
            user!!.updateEmail(email).addOnSuccessListener {
                val docRef = fStore!!.collection("users").document(user!!.uid)
                val edited: MutableMap<String, Any> = HashMap()
                edited["email"] = email
                edited["username"] = profileFullName?.getText().toString()
                edited["mobile"] = profilePhone?.getText().toString()
                docRef.update(edited).addOnSuccessListener {
                    Toast.makeText(this@EditProfile, "Profile Updated", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    finish()
                }
                Toast.makeText(this@EditProfile, "You changed successfully.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e -> Toast.makeText(this@EditProfile, e.message, Toast.LENGTH_SHORT).show() }
        })
        profileEmail?.setText(email)
        profileFullName?.setText(fullName)
        profilePhone?.setText(phone)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                val imageUri = data!!.data
                uploadImageToFirebase(imageUri)
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri?) {

        val fileRef = storageReference!!.child("users/" + fAuth!!.currentUser!!.uid + "/profile.jpg")
        fileRef.putFile(imageUri!!).addOnSuccessListener { fileRef.downloadUrl.addOnSuccessListener { uri -> Picasso.get().load(uri).into(profileImageView) } }.addOnFailureListener { Toast.makeText(applicationContext, "Failed.", Toast.LENGTH_SHORT).show() }
    }

    companion object {
        const val TAG = "TAG"
    }
}