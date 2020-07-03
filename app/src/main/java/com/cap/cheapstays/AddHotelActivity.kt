package com.cap.cheapstays

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.HashMap


class AddHotelActivity : AppCompatActivity() {
    var hotelimage: ImageView? = null
    var hotel_name: EditText? = null
    var hotel_rating: EditText? = null
    var hotel_price: EditText?=null
    var add_hotel_button : Button? = null
    var progressBar : ProgressBar? =  null
    var storageReference: StorageReference? = null
    var dataReference:DatabaseReference?=null
    var isImageAdded:Boolean=false

    lateinit var imageUri:Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addhotel)

        hotelimage = findViewById(R.id.addHotelImage)
        hotel_name = findViewById(R.id.hotel_name)
        hotel_rating = findViewById(R.id.hotel_rating)
        add_hotel_button = findViewById(R.id.add_hotel_button)
        progressBar = findViewById(R.id.progressBar)
        hotel_price=findViewById(R.id.add_hotel_price)

        dataReference= FirebaseDatabase.getInstance().reference.child("Hotel")
        storageReference = FirebaseStorage.getInstance().reference.child("HotelImage")


        hotelimage?.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 1000)
        })


        add_hotel_button?.setOnClickListener(View.OnClickListener
        {
            uploadFile()

        })


    }

    private fun uploadFile() {

        val key: String? = dataReference?.push()?.key
        storageReference?.child("$key.jpg")?.putFile(imageUri)?.addOnSuccessListener {
            storageReference!!.child("$key.jpg").downloadUrl.addOnSuccessListener { uri ->
                val hotel: HashMap<String, Any> = HashMap()

                hotel["hotelImageURL"] = uri.toString()
                hotel["hotelName"]=hotel_name?.text.toString()
                hotel["rating"] = hotel_rating?.text.toString()
                hotel["price"] = hotel_price?.text.toString()


                dataReference!!.child(key!!).setValue(hotel).addOnSuccessListener {
                    startActivity(Intent(this, HotelAdminActivity::class.java))
                }
            }


        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {


                imageUri = data?.data!!
                isImageAdded=true
                hotelimage?.setImageURI(imageUri)

            }

        }
    }

}







