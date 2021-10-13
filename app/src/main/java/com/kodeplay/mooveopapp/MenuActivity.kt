package com.kodeplay.mooveopapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.kodeplay.mooveopapp.ui.theme.MooveopAppTheme
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

class MenuActivity : ComponentActivity() {
    fun getMenu(chefName:String)
    {
        var menuJson = ""
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
//        val url = "https://mooveop.herokuapp.com/"+locationText
        val url = "https://mooveop.herokuapp.com/"+chefName
        println ("inside getMenu")
        println (url)
// Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                menuJson = "Response is: ${response.substring(0, 500)}"
                println (response)
                val menuData = Gson ().fromJson(
                    response,
                    Chef::class.java
                )
            },
            Response.ErrorListener {menuJson = "That didn't work!" })

// Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = getIntent()
        val chefName = intent.getStringExtra("chefName")

        if (chefName != null) {
            getMenu(chefName)
        }
        setContent {
//           Text ("Hello World")
            LazyColumn()
            {
                Image(
                    painter = painterResource(),
                    contentDescription = "Content description for visually impaired",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MooveopAppTheme {
        Greeting("Android")
    }
}