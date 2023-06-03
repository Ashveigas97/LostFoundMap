package com.example.lostfoundmap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.lostfoundmap.DatabaseHelper


class MainActivity : AppCompatActivity() {

    private val dbHelper: DatabaseHelper = DatabaseHelper(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val createAdvertButton = findViewById<Button>(R.id.createAdvertButton)
        val itemListButton = findViewById<Button>(R.id.itemListButton)
        val mapButton = findViewById<Button>(R.id.mapButton)

        createAdvertButton.setOnClickListener {
            startActivity(Intent(this, CreateNewAdvertActivity::class.java))
        }

        itemListButton.setOnClickListener {
            startActivity(Intent(this, ItemListActivity::class.java))
        }

        mapButton.setOnClickListener {

            startActivity(Intent(this, MapActivity::class.java))
        }


    }
}
