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





        val hotelKey: String?= intent.getStringExtra(getString(R.string.sp_HotelKey))
        Dataref = FirebaseDatabase.getInstance().reference.child(getString(R.string.Hotel_path))

        Dataref.child(hotelKey!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val ImageUrl = dataSnapshot.child(getString(R.string.hotelImageURL_id)).value.toString()
                    Picasso.get().load(ImageUrl).into(image)

                    val hotelname=dataSnapshot.child(getString(R.string.hotelName_id)).value.toString()
                    hotelName.text =hotelname

                    val hotelRat=dataSnapshot.child(getString(R.string.rating_id)).value.toString()
                    hotelRating.text =hotelRat

                    val hotelprice=dataSnapshot.child(getString(R.string.price_id)).value.toString()

                    hotelPrice.text =hotelprice

                    val hotelPhone=dataSnapshot.child(getString(R.string.phone_id)).value.toString()
                    phone.text=hotelPhone

                    val hotelfacility=dataSnapshot.child(getString(R.string.facility_id)).value.toString()
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
                        getString(R.string.Shouldnotbeblank),
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
                        getString(R.string.Shouldnotbeblank),
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
               name.error=getString(R.string.pleaseEnterName)
            }
            else{
                progressBarHotelInfo.visibility=View.VISIBLE

                val data: MutableMap<String, Any> = HashMap()
                data[getString(R.string.CustomerName)] = nameReceipt
                data[getString(R.string.NumberOfRooms)] = noOfRoomsReceipt
                data[getString(R.string.NumberOfChildren)] = noOfChildReceipt
                data[getString(R.string.NumberOfAdults)] = noOfAdultsReceipt
                data[getString(R.string.TypeOfBed)]=radioButtonBedsReceipt.toString()
                data[getString(R.string.TypeOfRoom)]=radioButtonRoomsReceipt.toString()

                db.collection(getString(R.string.Booking)).add(data)
                    .addOnSuccessListener { documentReference ->
                        documentID=documentReference.id
                        documentActivity(documentID)

                        progressBarHotelInfo.visibility=View.GONE

                    }
                    .addOnFailureListener { e ->
                      Toast.makeText(this,getString(R.string.ErrorAddingDocument),Toast.LENGTH_SHORT).show()
                    }



            }

        }



    }


 fun documentActivity(documentID: String?) {
        val sharedPref = getSharedPreferences(getString(R.string.documentKey), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(getString(R.string.DocID),documentID)
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
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
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
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
}