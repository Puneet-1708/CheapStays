package com.cap.cheapstays

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_hotel_info.*
import java.util.*


class HotelInfoActivity : AppCompatActivity() {

    lateinit var noOfAdultsReceipt: String
    lateinit var noOfRoomsReceipt: String
    lateinit var noOfChildReceipt: String
    lateinit var radioGroupForBeds: RadioGroup
    lateinit var radioGroupForRoomType: RadioGroup
    var radioButtonBedsReceipt:String?=null
    var radioButtonRoomsReceipt:String?=null
    lateinit var name:EditText
    lateinit var nameReceipt:String
     val button:Button?=null
    val TAG:String="ReceiptData"
    var documentID:String?=null
    lateinit var Dataref: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_info)

        val noOfAdults = resources.getStringArray(R.array.spinnerAdults)
        val noOfChildren = resources.getStringArray(R.array.spinnerChildren)
        val noOfRooms= resources.getStringArray(R.array.NoOfRooms)
        val name=findViewById<EditText>(R.id.etCustomer)
        val image=findViewById<ImageView>(R.id.hotelImageInfo)
        val hotelName=findViewById<TextView>(R.id.hotel_name_info)
        val hotelRating=findViewById<TextView>(R.id.hotel_rating_info)
        val hotelPrice=findViewById<TextView>(R.id.hotel_price_info)
        val phone=findViewById<TextView>(R.id.hotel_phone_info)
        val hotelFacility=findViewById<TextView>(R.id.hotel_facility_info)





        val hotelKey: String?= intent.getStringExtra("HotelKey")
        Dataref = FirebaseDatabase.getInstance().reference.child("Hotel")

        Dataref.child(hotelKey!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val ImageUrl = dataSnapshot.child("hotelImageURL").value.toString()
                    Picasso.get().load(ImageUrl).into(image)

                    val hotelname=dataSnapshot.child("hotelName").value.toString()
                    hotelName.text =hotelname

                    val hotelRat=dataSnapshot.child("rating").value.toString()
                    hotelRating.text =hotelRat

                    val hotelprice=dataSnapshot.child("price").value.toString()

                    hotelPrice.text =hotelprice

                    val hotelPhone=dataSnapshot.child("phone").value.toString()
                    phone.text=hotelPhone

                    val hotelfacility=dataSnapshot.child("facility").value.toString()
                   hotelFacility.text=hotelfacility

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


        val db= FirebaseFirestore.getInstance()


        //access the spinner
        val spinner = spinnerAdults
        if (spinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, noOfAdults)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    postion: Int,
                    id: Long
                ) {
                    noOfAdultsReceipt = parent.getItemAtPosition(postion).toString()
                }
            }
        }

        val spinner1 = spinnerChildren
        if (spinner1 != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, noOfChildren)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner1.adapter = adapter
            spinner1.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    postion: Int,
                    id: Long
                ) {
                    noOfChildReceipt = parent.getItemAtPosition(postion).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(
                        applicationContext,
                        "Should not be blank",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        val spinner3 = spinnerNoOfRooms
        if (spinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,noOfRooms)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner3.adapter = adapter
            spinner3.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    postion: Int,
                    id: Long
                ) {
                    noOfRoomsReceipt= parent.getItemAtPosition(postion).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(
                        applicationContext,
                        "Should not be blank",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        tvCheckin.setOnClickListener(View.OnClickListener {
            loadCalendar1()
        })
        tvCheckOut.setOnClickListener(View.OnClickListener {
            loadCalendar2()
        })


        radioGroupForBeds=findViewById(R.id.radioGroupBedsNo)
        radioGroupForBeds.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton =  findViewById(checkedId)
            radioButtonBedsReceipt=radioButton.text.toString()
        }

        radioGroupForRoomType=findViewById(R.id.radioGroupRoomType)
        radioGroupForRoomType.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton =  findViewById(checkedId)
            radioButtonRoomsReceipt=radioButton.text.toString()
        }

        confirmBooking.setOnClickListener {
            nameReceipt=name.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(nameReceipt)){
               name.error="Please Enter your Name"
            }
            else{
                progressBarHotelInfo.visibility=View.VISIBLE

                val data: MutableMap<String, Any> = HashMap()
                data["CustomerName"] = nameReceipt
                data["NumberOfRooms"] = noOfRoomsReceipt
                data["NumberOfChildren"] = noOfChildReceipt
                data["NumberOfAdults"] = noOfAdultsReceipt
                data["TypeOfBed"]=radioButtonBedsReceipt.toString()
                data["TypeOfRoom"]=radioButtonRoomsReceipt.toString()

                db.collection("Booking").add(data)
                    .addOnSuccessListener { documentReference ->
                        documentID=documentReference.id
                        documentActivity(documentID)

                        progressBarHotelInfo.visibility=View.GONE

                    }
                    .addOnFailureListener { e ->
                      Toast.makeText(this,"Error adding document",Toast.LENGTH_SHORT).show()
                    }



            }

        }



    }


 fun documentActivity(documentID: String?) {
        val sharedPref = getSharedPreferences("documentKey", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("DocID",documentID)
        editor.apply()
        val intent= Intent(this, BookingReceiptActivity::class.java)
        startActivity(intent)
    }


    private fun loadCalendar1() {
        val c = Calendar.getInstance()
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        val day = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                tvCheckin.setText("$month/$dayOfMonth/$year")



            }, year, month, day)
        datePickerDialog.show()
    }
    private fun loadCalendar2() {
        val c = Calendar.getInstance()
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        val day = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                tvCheckOut.setText("$month/$dayOfMonth/$year")



            }, year, month, day)
        datePickerDialog.show()
    }
}