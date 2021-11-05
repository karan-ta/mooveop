package com.kodeplay.mooveopapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.kodeplay.mooveopapp.ui.theme.MooveopAppTheme
class DeliveryActivity : ComponentActivity() {
    lateinit var isGateDelivery:MutableState<Boolean>
    lateinit var isFlatDelivery:MutableState<Boolean>
    lateinit var phoneNumberText:MutableState<String>
    lateinit var flatNumberText:MutableState<String>
    lateinit var buildingNameText:MutableState<String>
    lateinit var landmarkText:MutableState<String>

    fun validateAddressForm (errorString:MutableState<String>)
    {
        println (isGateDelivery.value)
        println (isFlatDelivery.value)
        println (phoneNumberText.value)
        println (flatNumberText.value)
        println (buildingNameText.value)
        println (landmarkText.value)

        if (!isFlatDelivery.value && !isGateDelivery.value)
        {
            errorString.value = "Select gate or flat delivery."
        }
        else if (phoneNumberText.value == "")
        {
            errorString.value = "Enter Phone Number"

        }
        else if (flatNumberText.value == "")
        {
           errorString.value = "Enter Flat Number"

        }
        else if (buildingNameText.value == "")
        {
            errorString.value = "Enter Building Name."
        }
        else if (landmarkText.value == "" )
        {
            errorString.value = "Enter Landmark Name."
        }
        else
        {
            errorString.value = ""
//            //send request to api to create order, save address.
//            const paramString = "&amount="+cartTotal*100+"&deliveryType="+data.deliveryAt+"&landmark="+data.landmark+"&buildingName="+data.buildingName+"&flatNumber="+data.flatNumber
//            fetch(process.env.api_url+"razorpaytesting",{
//                mode:"cors",
//                method: "POST",
//                headers: new Headers({
//                'Content-Type': 'application/x-www-form-urlencoded', // <-- Specifying the Content-Type
//            }),
//                body: paramString,


            val queue = Volley.newRequestQueue(this)
//        val url = "https://mooveop.herokuapp.com/"+locationText
            val url = "https://mooveop.herokuapp.com/19.2352291/72.8417576"
            println ("inside getRestaurants")
            println ("https://mooveop.herokuapp.com/"+locationText)

// Request a string response from the provided URL.
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                Response.Listener<String> { response ->
                    // Display the first 500 characters of the response string.
                    restaurantsJson = "Response is: ${response.substring(0, 500)}"
                    println ("inside get data callback")
                    println (response)
                    restaurantsData = Gson ().fromJson(
                        response,
                        Array<Chef>::class.java
                    )
                },
                Response.ErrorListener {restaurantsJson = "That didn't work!" })

// Add the request to the RequestQueue.
            queue.add(stringRequest)
        }
//        else if (isFlatDelivery && isGateDelivery)
//        {
//            setErrorString("Select either gate or flat delivery.")
//
//        }
//
//
//        else if (flatNumberRef.current.value == "")
//        {
//            setErrorString("Enter Flat Number")
//
//        }
//        else if (buildingName.label == undefined)
//        {
//            setErrorString("select building name.")
//        }
//        else if (landmarkInputRef.current.value == "" )
//        {
//            setErrorString("Enter landmark name.")
//
//        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var errorString = remember {mutableStateOf ("")}
            LazyColumn (modifier = Modifier.padding (
                start = 20.dp,
                end=20.dp
                    )){
                item{
                    Text(
                        modifier = Modifier.padding (top = 20.dp,bottom=20.dp),
                        text = "Enter Delivery address",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    Row(modifier = Modifier.padding(8.dp)) {
                       isGateDelivery  = remember { mutableStateOf(false) }
                        Checkbox(
                            checked = isGateDelivery.value,
                            onCheckedChange = { isGateDelivery.value = it },
                            enabled = true,
                            colors = CheckboxDefaults.colors(Color.Green)
                        )
                        Text(
                            modifier=Modifier.padding (start=10.dp),
                            text = "Deliver At Gate Entrance",
                            fontSize = 16.sp
                            )
                    }
                }
                item {
                    Row(modifier = Modifier.padding(8.dp)) {
                        isFlatDelivery = remember { mutableStateOf(false) }
                        Checkbox(
                            checked = isFlatDelivery.value,
                            onCheckedChange = { isFlatDelivery.value = it },
                            enabled = true,
                            colors = CheckboxDefaults.colors(Color.Green)
                        )
                        Text(
                            modifier=Modifier.padding (start=10.dp),
                            text = "Deliver At Flat Entrance",
                            fontSize = 16.sp

                        )
                    }
                }
                item {
                    Row(modifier = Modifier.padding(8.dp)){
                        phoneNumberText = remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = phoneNumberText.value,
                            onValueChange = { phoneNumberText.value = it },
                            label = { Text("Phone Number") }
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.padding(8.dp)){
                        flatNumberText = remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = flatNumberText.value,
                            onValueChange = { flatNumberText.value = it },
                            label = { Text("Flat Number") },
                            modifier = Modifier.width(120.dp)
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.padding(8.dp)){
                        buildingNameText = remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = buildingNameText.value,
                            onValueChange = { buildingNameText.value = it },
                            label = { Text("Building Name") },

                        )
                    }
                }
                item {
                    Row(modifier = Modifier.padding(8.dp)){
                        landmarkText =  remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = landmarkText.value,
                            onValueChange = { landmarkText.value = it },
                            label = { Text("Landmark") },
                            modifier = Modifier.height(120.dp)
                        )
                    }
                }
                item{
                    Button(modifier = Modifier.padding (top=25.dp),
                    onClick = { validateAddressForm (errorString)}) {
                        Text ("Pay Now to purchase.")
                    }
                }

                if (errorString.value != "")
                item{
                   Text ("${errorString.value}")
                }
            }
        }
    }
}
