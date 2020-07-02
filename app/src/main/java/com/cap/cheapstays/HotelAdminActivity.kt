package com.cap.cheapstays

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.sign

class HotelAdminActivity : AppCompatActivity() {

    lateinit var floatingActionButton: FloatingActionButton
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_admin)

        toolbar = findViewById(R.id.toolbarAdmin)
        setSupportActionBar(toolbar)

        floatingActionButton = findViewById(R.id.floatingActionButton)

        floatingActionButton.setOnClickListener(View.OnClickListener {

            startActivity(Intent(this, AddHotelActivity::class.java))
        })
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