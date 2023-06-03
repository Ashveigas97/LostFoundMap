package com.example.lostfoundmap


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ItemListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val itemList: MutableList<String> = mutableListOf()

    private val REMOVE_ITEM_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        listView = findViewById(R.id.listView)

        val dbHelper = DatabaseHelper(this)
        itemList.addAll(dbHelper.getAllItems())
        dbHelper.close()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedItemName = itemList[position]

            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra("itemName", selectedItemName)
            intent.putExtra("itemDescription", getItemDescription(selectedItemName))
            startActivityForResult(intent, REMOVE_ITEM_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REMOVE_ITEM_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val removedItem = data?.getStringExtra("removedItem")

            removedItem?.let {
                itemList.remove(it)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun getItemDescription(itemName: String): String {
        // Retrieve the item description based on the item name from the database
        val dbHelper = DatabaseHelper(this)
        val item = dbHelper.getItemByName(itemName)
        dbHelper.close()

        return item?.description ?: ""
    }
}