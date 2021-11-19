package com.mooveop.mooveopapp
import  android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kodeplay.mooveopapp.ui.theme.MooveopAppTheme
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

data class Chef(
    val signaturedishimagename:String = "",
    val chefphotoname:String = "",
    val chefprofilephoto:String = "",
    val chefname:String = "",
    val chefbio:String = "",
    val cuisinename:String = "",
    val likes:Int = 0,
    val id:Int = 0,
    val lat2:Double = 0.0,
    val long2:Double = 0.0,
    val lat2cosine:Double = 0.0,
    val menuList:List<MenuItem> = listOf()
)
data class MenuItem(
    val itemid:Int,
    val itemphotoname:String,
    val itemname:String,
    val itemdesc:String,
    val itemprice:String,
)

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQ_CODE = 1000
    private var restaurantsJson by mutableStateOf("")
   private var restaurantsData by mutableStateOf(emptyArray<Chef>())
    private var isSessionCart = false
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQ_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                } else {
                    // permission denied
                    Toast.makeText(this, "You need to grant permission to access location",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun getRestaurants (locationText:String)
    {
        restaurantsJson = ""
        // Instantiate the RequestQueue.
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
    fun getLocationDetails ()
    {
         var latitude: Double = 0.0
         var longitude: Double = 0.0
         var locationText by mutableStateOf("")
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE);
            return
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location ->
                // Got last known location. In some rare situations this can be null.
                latitude = location.latitude
                longitude = location.longitude
                locationText = "${latitude}/${longitude}"
                getRestaurants (locationText)
            }
    }
    fun showMenu(chefName:String)
    {
        val menuIntent = Intent(this, MenuActivity::class.java)
        menuIntent.putExtra("chefName",chefName)
        startActivity (menuIntent)
    }
    fun showCart()
    {
//        val cartIntent = Intent(this, ViewCartActivity::class.java)
//        startActivity (cartIntent)
        val signinIntent = Intent(this, Signin::class.java)
        startActivity (signinIntent)
    }
    fun getSessionCart ()
    {
        val sharedPreferences = getSharedPreferences("com.kodeplay.mooveopapp.prefs", MODE_PRIVATE)
        val json = sharedPreferences.getString("myShopMap", "")
        if (json != null) {
            isSessionCart = true
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLocationDetails()
        getSessionCart ()
        setContent {
            val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Open))
            val materialBlue700= Color(0xFF1976D2)
//                Button(onClick = { getLocationDetails() }) {
//                    Text("Click For Location")
//                }
//                Spacer(modifier = Modifier.padding(5.dp))
//                Button(onClick = {locationText = "" }) {
//                    Text("Clear Text")
//                }
//                Spacer(modifier = Modifier.padding(5.dp))
//                Button(onClick = {getRestaurants ()}) {
//                    Text("Get Restaurants Data")
//                }
//                Spacer(modifier = Modifier.padding(5.dp))
//                Text ("$locationText")
//                Spacer(modifier = Modifier.padding(5.dp))
//                Text ("$restaurantsJson")
                println (restaurantsData.size)
                if (restaurantsData.size > 0)
                    Scaffold(
                    bottomBar = {
                        if (isSessionCart)
                        BottomAppBar(modifier= Modifier
                            .clickable { showCart() }
                            ,
                        backgroundColor = materialBlue700,
                        )
                        {
                            Text("View Cart")
                        }
                                },
                      content={innerPadding ->LazyColumn(
                       modifier = Modifier
                           .fillMaxSize()
                           .padding(innerPadding)
                    ) {
                       for (myChef in restaurantsData) {

                           val id = resources.getIdentifier(
                               myChef.signaturedishimagename.replace(
                                   "/images/",
                                   ""
                               ).replace(".jpg", "").replace(".png", ""), "drawable", packageName
                           )
                           val chefPhotoId = resources.getIdentifier(
                               myChef.chefphotoname.replace("/images/", "").replace(".jpg", "")
                                   .replace(".png", ""), "drawable", packageName
                           )
                           println(myChef.chefname)
                           println(id)
                           println(chefPhotoId)
                           println(myChef.signaturedishimagename.replace("/images/", ""))

                           item {
                           Column(
                               modifier= Modifier
                                   .padding(start = 40.dp, end = 40.dp, top = 20.dp,bottom=10.dp)
                                   .border(0.2.dp, Color.Gray, RectangleShape)
                                   .clickable { showMenu(myChef.chefname) }
                           ){
                               Image(
                                   painter = painterResource(id = id),
                                   contentDescription = "Content description for visually impaired",
                                   modifier = Modifier.fillMaxSize()
                               )
                               Row (modifier=Modifier.padding(top=10.dp,end=10.dp,start=10.dp,bottom = 25.dp)){
                                   Column(Modifier.weight(2f)) {
                                       Text(
                                           text = myChef.chefname,
                                           modifier = Modifier.padding (5.dp) ,
                                           fontSize = 20.sp
                                       )
                                       Text(
                                           text = myChef.cuisinename,
                                           modifier = Modifier.padding (5.dp) ,
                                           fontSize = 16.sp
                                       )
                                   }
                                   Column(Modifier.weight(1f)) {
                                       Image(
                                           painter = painterResource(id = chefPhotoId),
                                           contentDescription = "Content description for visually impaired",
                                           modifier = Modifier
                                               .fillMaxSize()
                                               .clip(CircleShape)
                                               .border(2.dp, Color.Gray, CircleShape)   // add a border (optional)
                                       )
                                   }
                                   Spacer(modifier = Modifier.padding(bottom=10.dp))

                               }


                           }
                           }
                       }

                   }}
                    )
        }
    }
    @Composable
    fun trialScreen () {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            item {
                Column(
                    modifier=Modifier.padding(start=40.dp,end=40.dp,bottom = 20.dp)
                ){
                    Image(
                        painter = painterResource(R.drawable.signaturedish1),
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize()
                    )
                    Row (modifier=Modifier.padding(top=10.dp,end=10.dp,start=10.dp)){
                        Column(Modifier.weight(2f)) {
                            Text(
                                text = "Enerjio Dryfruits",
                                modifier = Modifier.padding (5.dp)

                            )
                            Text(
                                text = "Quality Dryfruits and seeds at your doorstep",
                                modifier = Modifier.padding (5.dp),
                                fontSize = 15.sp
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            Image(
                                painter = painterResource(R.drawable.wayne),
                                contentDescription = "",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                    }


                }
            }
            item {
                Column(
                    modifier=Modifier.padding(start=40.dp,end=40.dp,bottom = 20.dp)
                ){
                    Image(
                        painter = painterResource(R.drawable.sdolphinsignature),
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize()
                    )
                    Divider()
                    Row (modifier=Modifier.padding(top=10.dp)) {
                        Column(Modifier.weight(2f)) {
                            Text(
                                text = "S Dolphin",
                                modifier = Modifier.padding (5.dp)

                            )
                            Text(
                                text = "Bike and Car Wash at your society.",
                                modifier = Modifier.padding (5.dp)

                            )
                        }
                        Column(Modifier.weight(1f)) {
                            Image(
                                painter = painterResource(R.drawable.wayne),
                                contentDescription = "",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                    }


                }
            }
        }
    }
    @Preview
    @Composable
    fun trialPreview ()
    {
        trialScreen ()
    }

}