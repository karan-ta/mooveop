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
import kotlin.math.roundToInt

class GetRiderQuoteActivity : ComponentActivity() {
    var myPlacesData = mutableMapOf <String,Int?>()
    var totalCost = mutableStateOf(0)
    var previousTextLength = 0
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the SDK
        Places.initialize(applicationContext, "AIzaSyAActYUF3kZoA-KFzulfFGkLAXmx8oYzh4")

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)
        val token = AutocompleteSessionToken.newInstance()
        val bounds = RectangularBounds.newInstance(
            LatLng(19.199821, 72.842590),
            LatLng(19.251921, 72.868179),

        )
//        https://developers.google.com/maps/documentation/places/android-sdk/start#maps_places_get_started-kotlin
//        https://developers.google.com/maps/documentation/places/android-sdk/autocomplete#maps_places_programmatic_place_predictions-kotlin

        setContent {
            var expanded by remember { mutableStateOf(false) }
            var masterSuggestions = listOf ("karan", "priya", "vihaan")
            var suggestions = remember { mutableStateListOf <String>() }
            totalCost = remember {totalCost}
            var selectedText by remember { mutableStateOf("") }
            var dropDownWidth by remember { mutableStateOf(0) }

            Column() {
                OutlinedTextField(
                    value = selectedText,
                    onValueChange = {
                        previousTextLength = selectedText.length
                        selectedText = it
                        println("inside value change")
                        if (it.length > previousTextLength && it.length > 2)
                        {

                        val request =
                            FindAutocompletePredictionsRequest.builder()
                                // Call either setLocationBias() OR setLocationRestriction().
//                .setLocationBias(bounds)
//                                .setLocationRestriction(bounds)
                                .setOrigin(LatLng(19.23674, 72.84211))
                                .setCountries("IN")
//                .setTypeFilter(TypeFilter.ADDRESS)
                                .setSessionToken(token)
                                .setQuery(it)
                                .build()
                        placesClient.findAutocompletePredictions(request)
                            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                                suggestions.removeAll { true }
                                myPlacesData.clear()
                                for (prediction in response.autocompletePredictions) {
                                    Log.i("mooveop_place", prediction.placeId)
                                    Log.i(
                                        "mooveop_place",
                                        prediction.getPrimaryText(null).toString()
                                    )
                                    println(prediction.placeId)
                                    println(prediction.getPrimaryText(null).toString())
                                    println(prediction.distanceMeters.toString())
                                    if (!myPlacesData.containsKey(
                                            prediction.getPrimaryText(null).toString()
                                        )
                                    )
                                        myPlacesData.put(
                                            prediction.getPrimaryText(null).toString(),
                                            prediction.distanceMeters
                                        )
                                    if (!suggestions.contains(
                                            prediction.getPrimaryText(null).toString()
                                        )
                                    )
                                        suggestions.add(prediction.getPrimaryText(null).toString())
                                }
                            }.addOnFailureListener { exception: Exception? ->
                                if (exception is ApiException) {
                                    Log.e(
                                        "mooveop_placeAG",
                                        "Place not found: " + exception.statusCode
                                    )
                                    println("error in maps------ ")
                                }
                            }
//                        for (name in masterSuggestions)
//                        {
//                            if (name.startsWith(selectedText)) {
//                                suggestions.add(name)
//println (selectedText)
//                                println (name.startsWith(selectedText))
//                            }
//
//                        }
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
                            println (myPlacesData)
                            println (label)

                            println ((myPlacesData.get(selectedText)!! * 0.014).roundToInt())

                            totalCost.value = (myPlacesData.get(selectedText)!! * 0.014).roundToInt()
                        }) {
                            Text(text = label)
                        }
                    }
                }
                Spacer (modifier=Modifier.height(20.dp))
                Row{
                    OutlinedTextField(
                        label = {Text ("Pickup Time")}
                    )
                }
                Spacer (modifier=Modifier.height(20.dp))
                Row{
                    Text (
                        "Total Cost",
                        modifier=Modifier.padding(end=10.dp)
                    )
                    Text (totalCost.value.toString())
                }

            }
        }
    }}

