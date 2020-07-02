package com.cap.cheapstays

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_booking_receipt.*

class BookingReceiptActivity : AppCompatActivity() {

    val TAG: String="Receipt"
    var documentID:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_receipt)


        val sharedPref = getSharedPreferences("documentKey", Context.MODE_PRIVATE)
        documentID =sharedPref.getString("DocID","")

        val db= FirebaseFirestore.getInstance()


        db.collection("Booking").document(documentID!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {

                   tv_name.text= "1-> Your Name is: "+document.getString("CustomerName")
                    tv_noOfRooms.text="2-> Number of Rooms booked: "+document.getString("NumberOfRooms")
                    tv_noOfChildren.text="3-> Number of Children: "+document.getString("NumberOfChildren")
                    tv_noOfAdults.text= "4-> Number of Adult: "+document.getString("NumberOfAdults")
                    tv_bedType.text= "5-> Type of Bed: "+document.getString("TypeOfBed")
                    tv_roomType.text="6-> Type of Room: "+document.getString("TypeOfRoom")
                    Log.e(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.e(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }


        closeBooking.setOnClickListener {
            startActivity(Intent(this,HotelActivity::class.java))
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this@BookingReceiptActivity, HotelActivity::class.java))
    }
}