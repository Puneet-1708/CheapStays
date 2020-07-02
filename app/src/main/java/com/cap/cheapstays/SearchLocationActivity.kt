package com.cap.cheapstays

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

 class SearchLocationActivity : AppCompatActivity() {
     var adapter: ArrayAdapter<String>? = null
     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_search_location)
         val mySearchView = findViewById<View>(R.id.search) as SearchView
         val myList = findViewById<View>(R.id.listview) as ListView
         adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,resources.getStringArray(R.array.cities))
         myList.adapter = adapter
         mySearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
             override fun onQueryTextSubmit(query: String): Boolean {
                 mySearchView.clearFocus()
                 mySearchView.setQuery("", false)
                 return false
             }

             override fun onQueryTextChange(s: String): Boolean {
                 adapter!!.filter.filter(s)
                 if (myList.visibility != View.VISIBLE) {
                     myList.visibility = View.VISIBLE

                 }
                 return true
             }

         })

         myList.onItemClickListener =
             AdapterView.OnItemClickListener { parent, view, position, id ->
                 val select: String = myList.getItemAtPosition(0).toString()

                 if (select.equals("Bangalore")) {
                     val intent = Intent(this,HotelActivity::class.java)
                     startActivity(intent)
                 } else {
                     val builder = AlertDialog.Builder(this)
                     builder.setTitle(R.string.app_name)
                     builder.setMessage("Sorry, No Hotel service for this Location")
                     builder.show()
                 }

             }
     }
 }


