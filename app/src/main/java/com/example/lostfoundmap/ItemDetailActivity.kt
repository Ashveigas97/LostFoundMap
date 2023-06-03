package com.example.lostfoundmap
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ItemDetailActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvPhoneNumber: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvLocation: TextView
    private lateinit var btnRemoveItem: Button
    private lateinit var itemName: String
    private lateinit var dbHelper: DatabaseHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        tvName = findViewById(R.id.tvName)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)
        tvDescription = findViewById(R.id.tvDescription)
        tvDate = findViewById(R.id.tvDate)
        tvLocation = findViewById(R.id.tvLocation)
        btnRemoveItem = findViewById(R.id.btnRemoveItem)

        // Initialize the DatabaseHelper
        dbHelper = DatabaseHelper(this)

        itemName = intent.getStringExtra("itemName").toString()
        displayItemDetails()

        btnRemoveItem.setOnClickListener {
            removeItem()
        }
    }

    private fun displayItemDetails() {
        val item = dbHelper.getItemByName(itemName)

        item?.let {
            tvName.text = it.name
            tvPhoneNumber.text = it.phone
            tvDescription.text = it.description
            tvDate.text = it.date
            tvLocation.text = it.location
        }
    }

    private fun removeItem() {
        dbHelper.removeItem(itemName)
        dbHelper.close()

        Toast.makeText(this, "Item removed successfully", Toast.LENGTH_SHORT).show()

        // Pass the information of the removed item back to ItemListActivity
        val resultIntent = Intent()
        resultIntent.putExtra("removedItem", itemName)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}