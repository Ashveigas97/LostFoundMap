package com.example.lostfoundmap

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.location.Location
import java.io.Serializable

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    data class Item(
        val id: Long,
        val type: String,
        val name: String,
        val phone: String,
        val description: String,
        val date: String,
        val location: String
    ) : Serializable

    data class ItemLocation(
        val id: Int,
        val latitude: Double,
        val longitude: Double,
        val title: String?
    ):Serializable

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "advert.db"

        private const val TABLE_ITEMS = "items"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_LOCATION = "location"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_ADDRESS = "address"
        private const val COLUMN_IS_SELECTED = "isSelected"

    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_ITEMS " +
                "($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TYPE TEXT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_PHONE TEXT, " +
                "$COLUMN_DESCRIPTION TEXT, " +
                "$COLUMN_DATE TEXT, " +
                "$COLUMN_LOCATION TEXT, " +
                "$COLUMN_LATITUDE REAL, " +
                "$COLUMN_LONGITUDE REAL, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_ADDRESS TEXT)"

        db.execSQL(createTableQuery)
    }



    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
        }
    }



    fun insertItem(
        type: String,
        name: String,
        phone: String,
        description: String,
        date: String,
        location: String
    ): Long {
        val values = ContentValues()
        values.put(COLUMN_TYPE, type)
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_PHONE, phone)
        values.put(COLUMN_DESCRIPTION, description)
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_LOCATION, location)

        val db = this.writableDatabase
        val id = db.insert(TABLE_ITEMS, null, values)
        db.close()

        return id
    }



    fun removeItem(name: String) {
        val db = this.writableDatabase
        db.delete(TABLE_ITEMS, "$COLUMN_NAME=?", arrayOf(name))
        db.close()
    }

    @SuppressLint("Range")
    fun getAllItems(): List<String> {
        val itemList = mutableListOf<String>()
        val selectQuery = "SELECT * FROM $TABLE_ITEMS"

        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, null)

        cursor?.let {
            while (cursor.moveToNext()) {
                val itemName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                itemList.add(itemName)
            }
        }

        cursor?.close()
        db.close()

        return itemList
    }

    @SuppressLint("Range")
    fun getItemByName(name: String): Item? {
        val selectQuery = "SELECT * FROM $TABLE_ITEMS WHERE $COLUMN_NAME = ?"

        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, arrayOf(name))

        var item: Item? = null

        cursor?.let {
            if (cursor.moveToFirst()) {
                val itemId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val itemType = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE))
                val itemName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val itemPhone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE))
                val itemDescription = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
                val itemDate = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
                val itemLocation = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION))

                item = Item(
                    itemId,
                    itemType,
                    itemName,
                    itemPhone,
                    itemDescription,
                    itemDate,
                    itemLocation
                )
            }
        }

        cursor?.close()
        db.close()

        return item
    }


    @SuppressLint("Range")
    fun getAllItemLocations(): List<ItemLocation> {
        val itemLocations = mutableListOf<ItemLocation>()
        val selectQuery = "SELECT * FROM $TABLE_ITEMS"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery, null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                    val latitude = it.getDouble(it.getColumnIndex(COLUMN_LATITUDE))
                    val longitude = it.getDouble(it.getColumnIndex(COLUMN_LONGITUDE))
                    val title = it.getString(it.getColumnIndex(COLUMN_TITLE))
                    val itemLocation = ItemLocation(id, latitude, longitude, title)
                    itemLocations.add(itemLocation)
                } while (it.moveToNext())
            }
        }
        return itemLocations
    }

    @SuppressLint("Range")
    fun getLostAndFoundItemLocationsFromDatabase(): List<ItemLocation> {
        val itemLocations = mutableListOf<ItemLocation>()
        val query = "SELECT * FROM $TABLE_ITEMS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE))
            val longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE))
            val titleIndex = cursor.getColumnIndex(COLUMN_TITLE)
            val title = if (!cursor.isNull(titleIndex)) cursor.getString(titleIndex) else null
            val itemLocation = ItemLocation(id, latitude, longitude, title)
            itemLocations.add(itemLocation)
        }

        cursor.close()
        db.close()

        return itemLocations
    }




    // Inside DatabaseHelper class
    @SuppressLint("Range")
    fun getSavedLocationsFromDatabase(): MutableList<ItemLocation> {
        val itemLocations = mutableListOf<ItemLocation>()
        val query = "SELECT * FROM $TABLE_ITEMS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE))
            val longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE))
            val titleIndex = cursor.getColumnIndex(COLUMN_TITLE)
            val title = if (!cursor.isNull(titleIndex)) cursor.getString(titleIndex) else null
            val itemLocation = ItemLocation(id, latitude, longitude, title)
            itemLocations.add(itemLocation)
        }

        cursor.close()
        db.close()

        return itemLocations
    }
    @SuppressLint("Range")
    fun getSelectedAddressFromDatabase(): String? {
        val db = this.readableDatabase
        var selectedAddress: String? = null

        val query = "SELECT address FROM $TABLE_ITEMS" +
                " WHERE isSelected = 1 LIMIT 1"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            selectedAddress = cursor.getString(cursor.getColumnIndex("address"))
        }

        cursor.close()
        db.close()

        return selectedAddress
    }




}




