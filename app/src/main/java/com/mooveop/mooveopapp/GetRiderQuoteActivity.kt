package com.mooveop.mooveopapp
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
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
data class RiderRow(
    var toLocationSelectedText:String = "",
    var hoursText:String = "00",
    var minutesText:String = "00",
    var myIndex:Int = 0
)
class GetRiderQuoteActivity : ComponentActivity() {
    var myPlacesData = mutableMapOf <String,Int?>()
    var totalCost = mutableStateOf(0)
    var previousTextLength = 0
    var hoursText = mutableStateListOf ("00")
    var minutesText = mutableStateListOf("00")
    var selectedText = mutableStateListOf("")
    var riderCount = mutableStateOf (0)
    var riderRowsList = mutableStateListOf<RiderRow>()
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
        fun updateCurrentRowHoursText(theRowIndex:Int,theHoursText:String)
{
    println ("the index is" )
    println (theRowIndex)
    println (riderRowsList[0])
    println (riderRowsList[1])
            riderRowsList[theRowIndex] = riderRowsList[theRowIndex].copy(hoursText = theHoursText)
        }
        setContent {
            var expanded by remember { mutableStateOf(false) }
            var hoursExpanded by remember { mutableStateOf(false) }
            var minutesExpanded by remember { mutableStateOf(false) }
            var masterSuggestions = listOf ("karan", "priya", "vihaan")
            var suggestions = remember { mutableStateListOf <String>() }
            var hoursList = remember { mutableStateListOf <String>("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23") }
            var minutesList = remember { mutableStateListOf <String>("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59","60") }
            totalCost = remember {totalCost}
            riderCount = remember {riderCount}
             hoursText = remember {hoursText}
            riderRowsList = remember {riderRowsList}
            var minutesText = remember {minutesText}
            var selectedText = remember {selectedText}
            var dropDownWidth by remember { mutableStateOf(0) }
            Scaffold(
                floatingActionButton={
                    FloatingActionButton(
                        onClick = {
                            riderCount.value +=1
//                            hoursText.add("00")
//                            minutesText.add("00")
//                            selectedText.add("")
                                  riderRowsList.add (
                                      RiderRow(
                                          "",
                                          "00",
                                          "00",
                                          riderCount.value - 1

                                      )
                                          )
                                  },
                        content={
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null
                            )
                        }

                    )} ,
                    content={Column() {
                        var loopCounter = 0
                        repeat (riderRowsList.size) {
                            OutlinedTextField(
                                value = riderRowsList[loopCounter].toLocationSelectedText,
                                onValueChange = {
                                    previousTextLength =  riderRowsList[loopCounter].toLocationSelectedText.length
                                    riderRowsList[loopCounter].toLocationSelectedText = it
                                    println("inside value change")
                                    if (it.length > previousTextLength && it.length > 2) {
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
                                                    println(
                                                        prediction.getPrimaryText(null).toString()
                                                    )
                                                    println(prediction.distanceMeters.toString())
                                                    if (!myPlacesData.containsKey(
                                                            prediction.getPrimaryText(null)
                                                                .toString()
                                                        )
                                                    )
                                                        myPlacesData.put(
                                                            prediction.getPrimaryText(null)
                                                                .toString(),
                                                            prediction.distanceMeters
                                                        )
                                                    if (!suggestions.contains(
                                                            prediction.getPrimaryText(null)
                                                                .toString()
                                                        )
                                                    )
                                                        suggestions.add(
                                                            prediction.getPrimaryText(
                                                                null
                                                            ).toString()
                                                        )
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
                                label = { Text("label") }
                            )
                            DropdownMenu(
                                expanded = suggestions.isNotEmpty(),
                                onDismissRequest = { suggestions.removeAll { true } },
                                properties = PopupProperties(focusable = false),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                suggestions.forEach { label ->
                                    DropdownMenuItem(onClick = {

                                        riderRowsList[loopCounter].toLocationSelectedText= label
                                        println(myPlacesData)
                                        println(label)

                                        println((myPlacesData.get( riderRowsList[loopCounter].toLocationSelectedText)!! * 0.014).roundToInt())

                                        totalCost.value =
                                            (myPlacesData.get( riderRowsList[loopCounter].toLocationSelectedText)!! * 0.014).roundToInt()
                                    }) {
                                        Text(text = label)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Row() {

                                //only minutesText andminutesExpanded are different below
                                OutlinedTextField(
                                    value =  riderRowsList[loopCounter].minutesText,
                                    onValueChange = {  riderRowsList[loopCounter].minutesText = it },
                                    readOnly = true,
                                    modifier = Modifier
                                        .clickable { minutesExpanded = true }
                                        .width(140.dp)
                                        .padding(start = 20.dp, end = 20.dp),
                                    trailingIcon = {
                                        if (minutesExpanded == false) {
                                            IconButton(onClick = { minutesExpanded = true }) {
                                                Icon(
                                                    imageVector = Icons.Outlined.ArrowDropDown,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                        if (minutesExpanded == true) {
                                            IconButton(onClick = { minutesExpanded = false }) {
                                                Icon(
                                                    imageVector = Icons.Outlined.ArrowBack,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }
                                )
                                DropdownMenu(
                                    expanded = minutesExpanded,
                                    onDismissRequest = { minutesExpanded = false },
                                    properties = PopupProperties(focusable = false),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    minutesList.forEach { label ->
                                        DropdownMenuItem(onClick = {
                                            riderRowsList[loopCounter].minutesText = label
                                            minutesExpanded = false
                                        }) {
                                            Text(text = label)
                                        }
                                    }
                                }
                            }
                        }
                        Spacer (modifier=Modifier.height(20.dp))
                        Row{
                            Text (
                                "Total Cost",
                                modifier=Modifier.padding(end=10.dp)
                            )
                            Text (totalCost.value.toString())
                        }
                        Spacer (modifier=Modifier.height(10.dp))
                        Button(modifier = Modifier.padding (top=25.dp),
                            onClick = { }) {
                            Text ("Pay Now To Book.")
                        }
                    }}
                    )
                }
            @Composable
            fun hoursField()
            {
                OutlinedTextField(
                    value =  riderRowsList[loopCounter].hoursText,
                    onValueChange = {
                        updateCurrentRowHoursText ( riderRowsList[loopCounter].myIndex,it)
                    },
                    readOnly = true,
                    modifier = Modifier
                        .clickable { hoursExpanded = true }
                        .width(140.dp)
                        .padding(start = 20.dp, end = 20.dp),
                    trailingIcon = {
                        if (hoursExpanded == false) {
                            IconButton(onClick = { hoursExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowDropDown,
                                    contentDescription = null
                                )
                            }
                        }
                        if (hoursExpanded == true) {
                            IconButton(onClick = { hoursExpanded = false }) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
                DropdownMenu(
                    expanded = hoursExpanded,
                    onDismissRequest = { hoursExpanded = false },
                    properties = PopupProperties(focusable = false),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    hoursList.forEach { label ->
                        DropdownMenuItem(onClick = {
                            updateCurrentRowHoursText ( riderRowsList[loopCounter].myIndex,label)
                            hoursExpanded = false
                        }) {
                            Text(text = label)
                        }
                    }
                }
            }

        }
    }

