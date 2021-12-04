package com.mooveop.mooveopapp
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse

class GetRiderQuoteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the SDK
        Places.initialize(applicationContext, "AIzaSyAActYUF3kZoA-KFzulfFGkLAXmx8oYzh4")

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)
        val token = AutocompleteSessionToken.newInstance()
        val bounds = RectangularBounds.newInstance(
            LatLng(19.251921, 72.868179),
            LatLng(19.199821, 72.842590)
        )
//        https://developers.google.com/maps/documentation/places/android-sdk/start#maps_places_get_started-kotlin
//        https://developers.google.com/maps/documentation/places/android-sdk/autocomplete#maps_places_programmatic_place_predictions-kotlin
        val request =
            FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
//                .setLocationBias(bounds)
                .setLocationRestriction(bounds)
//                .setOrigin(LatLng(-33.8749937, 151.2041382))
                .setCountries("IN")
//                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery("aura yogi nagar")
                .build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                for (prediction in response.autocompletePredictions) {
                    Log.i("mooveop_place", prediction.placeId)
                    Log.i("mooveop_place", prediction.getPrimaryText(null).toString())
                    println (prediction.placeId)
                    println (prediction.getPrimaryText(null).toString())
                }
            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Log.e("mooveop_placeAG", "Place not found: " + exception.statusCode)
                    println ("error in maps------ ")
                }
            }

        setContent {
            var expanded by remember { mutableStateOf(false) }
            var masterSuggestions = listOf ("karan", "priya", "vihaan")
            var suggestions = remember { mutableStateListOf <String>() }
            var selectedText by remember { mutableStateOf("") }
            var dropDownWidth by remember { mutableStateOf(0) }
            Column() {
                OutlinedTextField(
                    value = selectedText,
                    onValueChange = {
                        println ("inside value change")
                        suggestions.removeAll{true}
                        selectedText = it
                        for (name in masterSuggestions)
                        {
                            if (name.startsWith(selectedText)) {
                                suggestions.add(name)
println (selectedText)
                                println (name.startsWith(selectedText))
                            }

                        }
                                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {Text ("label")}
                )
                DropdownMenu(
                    expanded = suggestions.isNotEmpty(),
                    onDismissRequest = { suggestions.removeAll{true} },
                    properties = PopupProperties(focusable = false),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    suggestions.forEach { label ->
                        DropdownMenuItem(onClick = {
                            selectedText = label
                        }) {
                            Text(text = label)
                        }
                    }
                }
            }
        }
    }}
