package com.cap.cheapstays

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.orhanobut.dialogplus.DialogPlus
import com.squareup.picasso.Picasso
import kotlin.math.sign

class HotelAdminActivity : AppCompatActivity() {

    lateinit var floatingActionButton: FloatingActionButton
    lateinit var toolbar: Toolbar
    var recyclerView: RecyclerView?=null
    var adapter: FirebaseRecyclerAdapter<Hotel,HotelAdminViewHolder>?=null
    lateinit var Dataref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_admin)


        toolbar = findViewById(R.id.toolbarAdmin)
        setSupportActionBar(toolbar)

        floatingActionButton = findViewById(R.id.floatingActionButton)

        floatingActionButton.setOnClickListener(View.OnClickListener {

            startActivity(Intent(this, AddHotelActivity::class.java))
        })

        Dataref = FirebaseDatabase.getInstance().reference.child("Hotel")

        recyclerView=findViewById(R.id.recyclerViewHotelAdmin)
        val query: com.google.firebase.database.Query = Dataref.orderByChild("Hotel")





        val options: FirebaseRecyclerOptions<Hotel> = FirebaseRecyclerOptions.Builder<Hotel>()
            .setQuery(query,Hotel::class.java)
            .build()

        adapter= object :
            FirebaseRecyclerAdapter<Hotel,HotelAdminViewHolder>(options){



            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):HotelAdminViewHolder {
                val view:View=
                    LayoutInflater.from(parent.context).inflate(R.layout.hotelcardadmin,parent,false)
                return HotelAdminViewHolder(view)
            }

            override fun onBindViewHolder(holder:HotelAdminViewHolder, position: Int, model: Hotel) {
                Picasso.get().load(model.hotelImageURL).into(holder.hotelImage)
                holder.hotelName.text = model.hotelName
                holder.hotelRating.text=model.rating
                holder.hotelPrice.text=model.price
               holder.deleteButton.setOnClickListener {
                    Dataref = FirebaseDatabase.getInstance().reference.child("Hotel").child(getRef(position).key.toString())
                   Dataref.removeValue().addOnSuccessListener {

                       Toast.makeText(this@HotelAdminActivity,"Data deleted",Toast.LENGTH_SHORT).show() }
               }

            }

        }

        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView?.adapter=adapter




    }

    class HotelAdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hotelName = itemView.findViewById<TextView>(R.id.hotelNameAdmin)
        val hotelRating = itemView.findViewById<TextView>(R.id.ratingsAdmin)
        val  hotelImage = itemView.findViewById<ImageView>(R.id.hotelImageAdmin)
        val deleteButton=itemView.findViewById<ImageView>(R.id.hotelDeleteButton)
        val hotelPrice=itemView.findViewById<TextView>(R.id.hotelPriceAdmin)
    }



    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.option_menu_admin, menu)
        return true

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> dialogBoxAbout()

            R.id.signOut ->signOut()

        }

        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        startActivity(Intent(this,AdminLoginActivity::class.java))
        FirebaseAuth.getInstance().signOut()
    }


    private fun dialogBoxAbout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("About")
        builder.setMessage("This is Hotel Bookings Management System App designed for the website CheapStays.com")
        builder.setPositiveButton("OK",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure want to quit?")
        builder.setPositiveButton("Yes", { dialogInterface: DialogInterface, i: Int -> finishAffinity() })
        builder.setNegativeButton("No",{ dialogInterface: DialogInterface, i: Int -> })
        builder.show()
    }



}