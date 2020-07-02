package com.cap.cheapstays

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class HotelActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var fStore : FirebaseFirestore?= null
    var storageReference: StorageReference? = null


    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    var fireBaseFireStore:FirebaseFirestore?=null
    var recyclerView:RecyclerView?=null
    var adapter:FirestoreRecyclerAdapter<Hotel,HotelViewHolder>? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel)
        fireBaseFireStore= FirebaseFirestore.getInstance()
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        fStore = FirebaseFirestore.getInstance()



        val key= fStore?.collection("Hotel")?.document()?.id
        val storageReference=FirebaseStorage.getInstance().reference.child("HotelImage")
      /*  val profileRef = storageReference!!.child("HotelImage/")
        profileRef.downloadUrl.addOnSuccessListener { uri -> Picasso.get().load(uri).into() }*/



        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)


        recyclerView=findViewById(R.id.recyclerViewHotel)

        //Query m limit or order by kr skte h

        val query:Query=fireBaseFireStore!!.collection("Hotel")

        

        //Recycler options

        val options:FirestoreRecyclerOptions<Hotel> = FirestoreRecyclerOptions.Builder<Hotel>()
            .setQuery(query,Hotel::class.java)
            .build()

         adapter= object :
            FirestoreRecyclerAdapter<Hotel, HotelViewHolder>(options){

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
            val view:View=LayoutInflater.from(parent.context).inflate(R.layout.hotelcard,parent,false)
                return HotelViewHolder(view)
            }

            override fun onBindViewHolder(holder: HotelViewHolder, position: Int, model: Hotel) {
             //  Picasso.get().load(model.hotelImageURL).into(holder.hotelImage)
                Picasso.get().load("https://image3.mouthshut.com/images/imagesp/925752868s.jpg").into(holder.hotelImage)
                holder.hotelName.text = model.hotelName
                holder.hotelRating.text=model.rating
                holder.button.setOnClickListener {
                    startActivity(Intent(this@HotelActivity, HotelInfoActivity::class.java))
                }
            }

        }

        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView?.adapter=adapter



    }




    class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hotelName = itemView.findViewById<TextView>(R.id.hotelName)
        val hotelRating = itemView.findViewById<TextView>(R.id.ratings)
      val  hotelImage = itemView.findViewById<ImageView>(R.id.hotelImage)
        val button=itemView.findViewById<Button>(R.id.hotelBookButton)
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(this@HotelActivity, ProfileActivity::class.java))
            }
            R.id.nav_logout -> {

                startActivity(Intent(this@HotelActivity, LoginActivity::class.java))
                signOut()
                Toast.makeText(this, "Successfully signed Out!", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.login_option_menu, menu)
        return true

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> dialogBoxAbout()
        }

        return super.onOptionsItemSelected(item)
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



