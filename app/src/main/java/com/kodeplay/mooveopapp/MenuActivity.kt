//https://stackoverflow.com/questions/7145606/how-do-you-save-store-objects-in-sharedpreferences-on-android
package com.kodeplay.mooveopapp
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.tooling.preview.Preview
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.kodeplay.mooveopapp.ui.theme.MooveopAppTheme
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

data class CartItem(
    val itemid:Int=0,
    val itemphotoname:String="",
    val itemname:String="",
    val itemdesc:String="",
    val itemprice:String="",
    var itemCartQuantity:Int=0,
    var theIndex:Int = 0
)

class MenuActivity : ComponentActivity() {
    private var menuData by mutableStateOf (Chef())
    private var chefName:String? = ""
    var myMenuMap = mutableMapOf <Int,CartItem>()
    var shopMap = mutableStateMapOf<String,MutableMap<Int,CartItem>>()
    var menuItemList = mutableStateListOf <CartItem>()
    var myShopMap = mutableMapOf <String,MutableList<CartItem>>()
    val PREFS_FILENAME = "com.kodeplay.mooveopapp.prefs"
    var isSessionCart:MutableState<Boolean> = mutableStateOf (false)
//    var mPrefs = getPreferences(MODE_PRIVATE)
    fun getMenu(chefName:String)
    {//merge itemcartquantity from myShopmap
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
                 menuData = Gson ().fromJson(
                    response,
                    Chef::class.java
                )
                var theIndex = 0
                var currentItemCartQuantity = 0
                for (myMenuItem in menuData.menuList) {
                    var currentItemCartQuantity = 0
                    if (myMenuMap.containsKey(myMenuItem.itemid)) {
                            currentItemCartQuantity =
                                myMenuMap[myMenuItem.itemid]!!.itemCartQuantity
                        }
                    menuItemList.add (
                        CartItem(
                            myMenuItem.itemid,
                            myMenuItem.itemphotoname,
                            myMenuItem.itemname,
                            myMenuItem.itemdesc,
                            myMenuItem.itemprice,
                            currentItemCartQuantity

                        )
                            )
                    theIndex++
                }
            },
            Response.ErrorListener {menuJson = "That didn't work!" })

// Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
    fun showCart()
    {
        // we assume that cart is updated whenever the view cart button is clicked as update to shared preferences looks cheap
        val sharedPreferences =  getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val cartIntent = Intent(this, ViewCartActivity::class.java)
        val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(myShopMap)
        println ("my shop map json string")
        println (json)
        prefsEditor.putString("myShopMap", json)
        prefsEditor.commit()
        startActivity (cartIntent)
    }
    fun getSessionCart ()
    {
        val sharedPreferences = getSharedPreferences("com.kodeplay.mooveopapp.prefs", MODE_PRIVATE)
        val json = sharedPreferences.getString("myShopMap", "")
        if (json != null) {
            isSessionCart.value = true
            val type: Type = object : TypeToken<MutableMap<String, MutableList<CartItem>>>() {}.type
            myShopMap = Gson().fromJson<MutableMap<String, MutableList<CartItem>>>(json, type)
            val cartItemList = myShopMap[chefName]
            if (cartItemList != null) {
                for (theCartItem in cartItemList) {
                    myMenuMap[theCartItem.itemid] = theCartItem
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = getIntent()
        chefName = intent.getStringExtra("chefName")

        if (chefName == null) {
            setContent {
                Text ("Shop Name not received. Please contact - 9820011185")
            }
            }
        else {
            getMenu(chefName!!)
            setContent {
                isSessionCart = remember{ mutableStateOf(false)}
                getSessionCart ()
                var myMenuItemList = remember {menuItemList}
                fun updateMenuItemCartQuantity (theIndex:Int,mode:String)
                {
                    val cartCountBeforeClick = myMenuItemList[theIndex].itemCartQuantity
                    if (mode == "add") {
                        isSessionCart.value = true
                        myMenuItemList[theIndex] =
                            myMenuItemList[theIndex].copy(itemCartQuantity = cartCountBeforeClick + 1)
                    }
                    if (mode == "subtract")
                    {
                        myMenuItemList[theIndex] =
                            myMenuItemList[theIndex].copy(itemCartQuantity = cartCountBeforeClick - 1)
                    }
                    myShopMap[chefName!!] = myMenuItemList.filter{it.itemCartQuantity>0} as MutableList<CartItem>
                   var isSessionCartTemp = false
                   for (thelist in myShopMap.values)
                   {
                       if (thelist.size > 0) {
                           isSessionCartTemp = true
                           break
                       }
                   }
                    isSessionCart.value = isSessionCartTemp
                    println (myShopMap)
                    println (myMenuItemList)
                }
                val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Open))
                val materialBlue700= Color(0xFF1976D2)
                Scaffold(
                    bottomBar = {
                        if (isSessionCart.value)
                            BottomAppBar(modifier=Modifier.clickable { showCart () },
                                backgroundColor = materialBlue700
                            )
                            {
                                Text("View Cart")
                            }
                    },
                    content= {
                        if (menuData.menuList.size > 0) {
                            println(menuData.chefprofilephoto)
                            println(menuData.chefphotoname)
                            val profilePhotoId = resources.getIdentifier(
                                menuData.chefprofilephoto.replace(
                                    "/images/",
                                    ""
                                ).replace(".jpg", "").replace(".png", ""), "drawable", packageName
                            )
                            val chefPhotoId = resources.getIdentifier(
                                menuData.chefphotoname.replace(
                                    "/images/",
                                    ""
                                ).replace(".jpg", "").replace(".png", ""), "drawable", packageName
                            )

                            LazyColumn()
                            {
                                item {
                                    Column (modifier=Modifier.padding(bottom=20.dp)){
                                        Image(
                                            painter = painterResource(id = profilePhotoId),
                                            contentDescription = "Content description for visually impaired",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(bottom = 30.dp)
                                        )

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(80.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(
                                                painter = painterResource(id = chefPhotoId),
                                                contentDescription = "Content description for visually impaired",
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .border(2.dp, Color.Gray, CircleShape)
                                            )
                                        }
                                        Column(
                                            modifier = Modifier.padding(
                                                top = 20.dp,
                                                start = 20.dp
                                            )
                                        )
                                        {
                                            Text(
                                                text = menuData.chefname,
                                                modifier = Modifier.padding(5.dp),
                                                fontSize = 20.sp
                                            )
                                            Text(
                                                text = menuData.cuisinename,
                                                modifier = Modifier.padding(5.dp),
                                                fontSize = 16.sp
                                            )
                                        }
                                        var theIndex = 0
                                        for (myMenuItem in myMenuItemList) {

                                            var itemPhotoId = resources.getIdentifier(
                                                myMenuItem.itemphotoname.replace(
                                                    "/images/",
                                                    ""
                                                ).replace(".jpg", "").replace(".jpeg", "")
                                                    .replace(".png", ""), "drawable", packageName
                                            )
                                            Column(
                                                modifier = Modifier
                                                    .padding(
                                                        start = 20.dp,
                                                        end = 20.dp,
                                                        top = 20.dp,
                                                        bottom = 15.dp
                                                    )
                                                    .border(0.2.dp, Color.Gray, RectangleShape)
                                            )
                                            {
                                                Image(
                                                    painter = painterResource(id = itemPhotoId),
                                                    contentDescription = "",
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                                Row(
                                                    modifier = Modifier.padding(
                                                        top = 10.dp,
                                                        start = 3.dp
                                                    )
                                                ) {
                                                    Column(Modifier.weight(4f)) {
                                                        Text(
                                                            text = myMenuItem.itemname,
                                                            modifier = Modifier.padding(5.dp),
                                                            fontSize = 20.sp
                                                        )
                                                    }
                                                    Column(
                                                        Modifier
                                                            .weight(1f)
                                                            .fillMaxSize()
                                                    ) {
                                                        Text(
                                                            text = myMenuItem.itemprice,
                                                            modifier = Modifier.padding(5.dp),
                                                            fontSize = 20.sp
                                                        )
                                                    }

                                                }
                                                Column(modifier = Modifier.padding(start = 3.dp)) {
                                                    Text(
                                                        text = myMenuItem.itemdesc,
                                                        modifier = Modifier.padding(5.dp),
                                                        fontSize = 16.sp
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.padding(
                                                        start = 8.dp,
                                                        top = 10.dp
                                                    )
                                                ) {
                                                    Button(onClick = {
                                                        updateMenuItemCartQuantity(
                                                            myMenuItem.theIndex, "subtract"
                                                        )
                                                    }) {
                                                        Text("+")
                                                    }
                                                    Text(
                                                        text = "${myMenuItem.itemCartQuantity}",
                                                        fontSize = 25.sp,
                                                        modifier = Modifier.padding(
                                                            start = 10.dp,
                                                            end = 10.dp
                                                        )
                                                    )
                                                    Button(onClick = {
                                                        updateMenuItemCartQuantity(
                                                            myMenuItem.theIndex, "add"
                                                        )
                                                    }) {
                                                        Text("+")
                                                    }
                                                }
                                            }
                                            theIndex++
                                        }
                                        Button(onClick = { showCart() }) {
                                            Text("View cart")
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                modifier = Modifier.padding(10.dp),
                                text = "Loading Data. Please Hold On."
                            )
                        }
                    })
            }
        }
    }
}

@Composable
fun menuActivityTrial ()
{

    LazyColumn ()
    {
        item {
            Image(
                painter = painterResource(R.drawable.enerjioprofile),
                contentDescription = "",
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxSize()
                    .border(1.dp, Color.Red, RectangleShape)
            )
            Column (
                modifier=Modifier.padding(start=40.dp,end=40.dp)
                    ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Image(
                        painter = painterResource(R.drawable.wayne),
                        contentDescription = "",
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                            .padding(bottom = 10.dp)
                    )
                }
                Text(
                    text = "S.Dolphin",
                    modifier = Modifier.padding (5.dp),
                    fontSize = 20.sp

                )
                Text(
                    text = "Premium Door-Step Bike and Car cleaning service." ,
                    modifier = Modifier.padding (start=5.dp,end=5.dp,top=5.dp,bottom=20.dp),
                    fontSize = 16.sp
                )
                Image(
                    painter = painterResource(R.drawable.signaturedish1),
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize()
                )
                Row {
                    Column(Modifier.weight(4f)) {
                        Text(
                            text = "Hatchback or Sedan",
                            modifier = Modifier.padding(5.dp),
                            fontSize = 20.sp
                        )
                    }
                    Column(
                        Modifier
                            .weight(1f)
                            .fillMaxSize()) {
                        Text(
                            text = "600",
                            modifier = Modifier.padding(5.dp),
                            fontSize = 20.sp
                        )
                    }

                }
                Text(
                    text = "Full Exterior and Interior cleaning with polish.",
                    modifier = Modifier.padding(5.dp),
                    fontSize = 16.sp
                )
               Row{
                   IconButton(modifier = Modifier.
                   then(Modifier.size(24.dp)),
                       onClick = { }) {
                       Icon(
                           Icons.Filled.Add,
                           "contentDescription",
                           tint = Color.White)
                   }
                   Text(
                       text="0",
                       fontSize = 25.sp,
                       modifier=Modifier.padding(start=10.dp,end=10.dp)
                   )
                   Button(onClick = { /*TODO*/ }) {
                       Text("+")
                   }
               }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    menuActivityTrial ()
//    MooveopAppTheme {
//        Greeting("Android")
//    }
}