package com.cap.cheapstays

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import java.util.HashMap

class AddHotelActivity : AppCompatActivity() {
    var hotelimage: ImageView? = null
    var hotel_name: EditText? = null
    var hotel_rating: EditText? = null
    var hotel_description: EditText? = null
    var add_hotel_button : Button? = null
    var progressBar : ProgressBar? =  null
    var fStore : FirebaseFirestore?= null
    var fileRef: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addhotel)

        hotelimage = findViewById(R.id.addHotelImage)
        hotel_name = findViewById(R.id.hotel_name)
        hotel_rating = findViewById(R.id.hotel_rating)
      //  hotel_description = findViewById(R.id.hotel_description)
        add_hotel_button = findViewById(R.id.add_hotel_button)
        progressBar = findViewById(R.id.progressBar)

        fStore = FirebaseFirestore.getInstance()

        fileRef = FirebaseStorage.getInstance().reference.child("HotelImage")


        hotelimage?.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 1000)
        })


       add_hotel_button?.setOnClickListener(View.OnClickListener
        {
            if (hotel_name?.text.toString().isEmpty() || hotel_rating?.text.toString()
                    .isEmpty() || hotel_description?.text.toString().isEmpty()
            ) {
                Toast.makeText(
                    this@AddHotelActivity,
                    "One or Many fields are empty.",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
        })


    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                val imageUri = data!!.data
                val key= fStore?.collection("Hotel")?.document()?.id
                //Upload hotel image to  firebase storage
                fileRef?.child("$key.jpg")?.putFile(imageUri!!)!!.addOnSuccessListener {


                    // storing in hotel table
                    fileRef?.child("$key.jpg")!!.downloadUrl?.addOnSuccessListener {
                        val documentReference = fStore!!.collection("Hotel")
                        val hotel: MutableMap<String, Any> = HashMap()
                        hotel["hotelImageURL"] = imageUri.toString()
                        hotel["hotelName"]=hotel_name?.text.toString()
                        hotel["rating"] = hotel_rating?.text.toString()
                      //  hotel["hotel_description"] = hotel_description?.text.toString()
                        documentReference.document().set(hotel).addOnSuccessListener {
                            Toast . makeText (
                                this@AddHotelActivity,
                                "Hotel image Successfully uploaded.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
            }

        }
    }

}







