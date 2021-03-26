package com.hobarb.locatadora.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.hobarb.locatadora.R
import com.hobarb.locatadora.utilities.CONSTANTS
import com.hobarb.locatadora.utilities.secrets
import java.util.*

class SelectDestinationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_destination)

        val destination:TextView = findViewById(R.id.tv_destination_ac_selDest)
        val latLong:TextView = findViewById(R.id.tv_latLong_ac_selDest)

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), secrets.API_KEY, Locale.US);
        }


        // Initialize the AutocompleteSupportFragment.


        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.

                destination.setText(""+place.name)
                latLong.setText(""+place.latLng)

            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                destination.setText(""+status.statusMessage)
                latLong.setText(""+status.resolution)

            }
        })

    }
}