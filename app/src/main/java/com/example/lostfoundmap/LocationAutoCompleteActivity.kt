package com.example.lostfoundmap

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale

class LocationAutoCompleteActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddressAdapter
    private val LOCATION_AUTOCOMPLETE_REQUEST_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_auto_complete)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AddressAdapter()
        recyclerView.adapter = adapter

        // Initialize your location prediction service or API client
        val context: Context = this // 'this' refers to the current activity
        val locationPredictionService = LocationPredictionService(context)

        // Set up a listener for text input changes
        val inputText = findViewById<TextInputEditText>(R.id.inputText)
        inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Retrieve the initials typed by the user
                val initials = s.toString()

                // Make a request to your location prediction service to get address predictions
                val predictions = locationPredictionService.getPredictions(initials, "Australia")

                // Update the adapter with the new predictions
                adapter.updatePredictions(predictions)
            }
        })

        adapter.setOnItemClickListener(object : AddressAdapter.OnItemClickListener {
            override fun onItemClick(address: Address) {
                val selectedAddress = buildAddressString(address)

                // Create an Intent to pass the selected address back to CreateNewAdvertActivity
                val resultIntent = Intent()
                resultIntent.putExtra("selectedAddress", selectedAddress)
                setResult(Activity.RESULT_OK, resultIntent)

                // Finish the LocationAutoCompleteActivity
                finish()
            }
        })
    }

    private fun buildAddressString(address: Address): String {
        val stringBuilder = StringBuilder()

        // Append the address lines
        for (i in 0 until address.maxAddressLineIndex) {
            stringBuilder.append(address.getAddressLine(i))
            stringBuilder.append(", ")
        }

        // Append the city
        if (address.locality != null) {
            stringBuilder.append(address.locality)
            stringBuilder.append(", ")
        }

        // Append the state
        if (address.adminArea != null) {
            stringBuilder.append(address.adminArea)
            stringBuilder.append(", ")
        }

        // Append the country
        if (address.countryName != null) {
            stringBuilder.append(address.countryName)
        }

        return stringBuilder.toString()
    }

    class LocationPredictionService(private val context: Context) {

        fun getPredictions(initials: String, country: String): MutableList<Address>? {
            val geocoder = Geocoder(context, Locale.getDefault())

            // Get the latitude and longitude for the provided initials
            val address = "$initials, $country"
            val addresses = geocoder.getFromLocationName(address, 5) // Adjust the maximum number of addresses as per your requirement

            return addresses
        }
    }

    class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
        private val predictions: MutableList<Address> = mutableListOf()
        private var listener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onItemClick(address: Address)
        }

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_address_prediction, parent, false)
            return AddressViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
            val prediction = predictions[position]
            holder.bind(prediction)

            holder.itemView.setOnClickListener {
                listener?.onItemClick(prediction)
            }
        }

        override fun getItemCount(): Int {
            return predictions.size
        }

        @SuppressLint("NotifyDataSetChanged")
        fun updatePredictions(newPredictions: List<Address>?) {
            predictions.clear()
            if (newPredictions != null) {
                predictions.addAll(newPredictions)
            }
            notifyDataSetChanged()
        }

        class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(prediction: Address) {
                val predictionTextView = itemView.findViewById<TextView>(R.id.predictionTextView)
                predictionTextView.text = getAddressText(prediction)
            }

            private fun getAddressText(address: Address): String {
                // Replace with the appropriate method to get the address text
                // For example, you can use address.getAddressLine(0) to get the first address line
                return address.getAddressLine(0) ?: ""
            }
        }
    }
}

