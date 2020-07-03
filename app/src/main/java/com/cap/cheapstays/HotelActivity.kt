package com.cap.cheapstays

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class HotelActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    var recyclerView:RecyclerView?=null
   var adapter:FirebaseRecyclerAdapter<Hotel,HotelViewHolder>?=null
    lateinit var Dataref: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel)


        Dataref = FirebaseDatabase.getInstance().reference.child("Hotel")

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)


        recyclerView=findViewById(R.id.recyclerViewHotel)



        val query: com.google.firebase.database.Query = Dataref.orderByChild("Hotel")

        



        val options:FirebaseRecyclerOptions<Hotel> = FirebaseRecyclerOptions.Builder<Hotel>()
            .setQuery(query,Hotel::class.java)
            .build()

         adapter= object :
           FirebaseRecyclerAdapter<Hotel, HotelViewHolder>(options){

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
            val view:View=LayoutInflater.from(parent.context).inflate(R.layout.hotelcard,parent,false)
                return HotelViewHolder(view)
            }

            override fun onBindViewHolder(holder: HotelViewHolder, position: Int, model: Hotel) {
              Picasso.get().load(model.hotelImageURL).into(holder.hotelImage)
                holder.hotelName.text = model.hotelName
                holder.hotelRating.text=model.rating
                holder.price.text=model.price

                holder.button.setOnClickListener {
                    val intent = Intent(this@HotelActivity,HotelInfoActivity::class.java)
                    intent.putExtra("HotelKey", getRef(position).key)
                    startActivity(intent)
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
        val price=itemView.findViewById<TextView>(R.id.hotelPrice)

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



