package com.cap.cheapstays

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AboutHotelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_hotel)
    }

    override fun onBackPressed() {
        startActivity(Intent(this@AboutHotelActivity, HotelActivity::class.java))
    }
}