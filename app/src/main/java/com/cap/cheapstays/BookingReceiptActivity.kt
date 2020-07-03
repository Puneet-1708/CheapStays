package com.cap.cheapstays

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_booking_receipt.*

class BookingReceiptActivity : AppCompatActivity() {

    var documentID:String?=null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_receipt)


        val sharedPref = getSharedPreferences(getString(R.string.documentKey), Context.MODE_PRIVATE)
        documentID =sharedPref.getString(getString(R.string.DocID),"")

        val db= FirebaseFirestore.getInstance()


        db.collection(getString(R.string.Booking)).document(documentID!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {

                   tv_name.text= getString(R.string.yourName)+document.getString(getString(R.string.CustomerName))
                    tv_noOfRooms.text=getString(R.string.roomBooked)+document.getString(getString(R.string.NumberOfRooms))
                    tv_noOfChildren.text=getString(R.string.noOfChildren)+document.getString(getString(R.string.NumberOfChildren))
                    tv_noOfAdults.text= getString(R.string.noOfAdult)+document.getString(getString(R.string.NumberOfAdults))
                    tv_bedType.text= getString(R.string.BedType)+document.getString(getString(R.string.TypeOfBed))
                    tv_roomType.text=getString(R.string.roomType)+document.getString(getString(R.string.TypeOfRoom))
                } else {
                    Toast.makeText(this, getString(R.string.ToastNoDoc), Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, getString(R.string.UnabletogenerateReceipt), Toast.LENGTH_SHORT)
                    .show()
            }


        closeBooking.setOnClickListener {
            startActivity(Intent(this,HotelActivity::class.java))
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this@BookingReceiptActivity, HotelActivity::class.java))
    }
}