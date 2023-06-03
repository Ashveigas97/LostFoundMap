@file:Suppress("DEPRECATION")

package com.example.lostfoundmap

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class CreateNewAdvertActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButtonLost: RadioButton
    private lateinit var radioButtonFound: RadioButton
    private lateinit var editTextName: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var buttonGetCurrentLocation: Button
    private lateinit var buttonSave: Button

    private lateinit var dbHelper: DatabaseHelper
    private val LOCATION_AUTOCOMPLETE_REQUEST_CODE = 1
    private val REQUEST_LOCATION_PERMISSION = 100

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_advert)

        dbHelper = DatabaseHelper(this)

        radioButtonLost = findViewById(R.id.lostRadioButton)
        radioButtonFound = findViewById(R.id.foundRadioButton)
        editTextName = findViewById(R.id.nameEditText)
        editTextPhone = findViewById(R.id.phoneEditText)
        editTextDescription = findViewById(R.id.descriptionEditText)
        editTextDate = findViewById(R.id.dateEditText)
        editTextLocation = findViewById(R.id.locationEditText)
        buttonGetCurrentLocation = findViewById(R.id.getCurrentLocationButton)
        buttonSave = findViewById(R.id.saveButton)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        editTextLocation.setOnClickListener {
            val intent = Intent(this, LocationAutoCompleteActivity::class.java)
            startActivityForResult(intent, LOCATION_AUTOCOMPLETE_REQUEST_CODE)
        }

        buttonGetCurrentLocation.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getCurrentLocation()
            } else {
                requestLocationPermission()
            }

            // Set the default location
            editTextLocation.setText("Waurn Ponds, Geelong, Australia")
        }


        buttonSave.setOnClickListener {
            val postType = if (radioButtonLost.isChecked) "Lost" else "Found"
            val name = editTextName.text.toString()
            val phone = editTextPhone.text.toString()
            val description = editTextDescription.text.toString()
            val date = editTextDate.text.toString()
            val location = editTextLocation.text.toString()

            dbHelper.insertItem(postType, name, phone, description, date, location)
            dbHelper.close()

            Toast.makeText(this, "Item saved successfully.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSION
        )
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    if (addresses?.isNotEmpty() == true) {
                        val address = addresses[0]
                        val fullAddress = "${address.latitude}, ${address.longitude}"
                        editTextLocation.setText(fullAddress)
                    }
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                // Handle permission denial
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_AUTOCOMPLETE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedAddress = data?.getStringExtra("selectedAddress")
            editTextLocation.setText(selectedAddress)
        }
    }
}
