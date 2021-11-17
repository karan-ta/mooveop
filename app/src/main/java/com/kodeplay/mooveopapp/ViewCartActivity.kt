package com.kodeplay.mooveopapp
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kodeplay.mooveopapp.ui.theme.MooveopAppTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ViewCartActivity : ComponentActivity() {
//    var mPrefs = getPreferences(MODE_PRIVATE)
//    var myShopMap = mutableMapOf <String,MutableList<CartItem>>()
    private var cartTotalValue:Double = 0.0
     private var myShopMap = mutableStateMapOf<String,MutableList<CartItem>>()
    lateinit private var myShopMapRem:SnapshotStateMap<String, MutableList<CartItem>>
    private var cartTotal = mutableStateOf (0.0)
    val PREFS_FILENAME = "com.kodeplay.mooveopapp.prefs"
fun showDeliveryAddressPage()
{
    val deliveryIntent = Intent(this, DeliveryActivity::class.java)
    startActivity (deliveryIntent)
    updateSharedPreferences ()
}
fun updateSharedPreferences (){
    val sharedPreferences =  getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
    val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()
    val json = Gson().toJson(myShopMapRem)
    println ("my shop map json string")
    println (json)
    prefsEditor.putString("myShopMap", json)
    prefsEditor.commit()
}
    override fun onBackPressed ()
    {
        updateSharedPreferences ()
        super.onBackPressed()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    val sharedPreferences = getSharedPreferences("com.kodeplay.mooveopapp.prefs", MODE_PRIVATE)
    val json = sharedPreferences.getString("myShopMap", "")
        val type: Type = object : TypeToken<MutableMap<String,MutableList<CartItem>>>() {}.type
         var shopMap =  Gson().fromJson<MutableMap<String,MutableList<CartItem>>>(json, type)
        for (itemLists in shopMap.values)
        {
            for (myItem in itemLists)
                cartTotal.value = cartTotal.value + myItem.itemCartQuantity * myItem.itemprice.toDouble()
        }
       myShopMap.putAll(shopMap)
    println (myShopMap.keys)
    println (myShopMap.values)
        setContent {
             myShopMapRem = remember {myShopMap}
            var cartTotalRem = remember {cartTotal}
            fun removeCartItem (shopName:String,theItem:CartItem)
            {
                //remove item from myShopMap
                //write myShopMap in shared preferences
                //update Total
                var theList = myShopMapRem[shopName]?.filter{it.itemid != theItem.itemid}
                cartTotalRem.value = cartTotalRem.value - theItem.itemprice.toDouble()
                myShopMapRem[shopName] = theList as MutableList<CartItem>
                //update shared preference.
            }
            LazyColumn {
                item {
                    Column(
                        modifier= Modifier.padding(start = 40.dp, end = 40.dp, top = 20.dp,bottom=10.dp)
                    )
                    {
                        Text(
                            text = "Your Order",
                            style = MaterialTheme.typography.h4,
                            modifier=Modifier.padding (bottom = 20.dp),
                            fontWeight = FontWeight.Bold
                        )
                        for (shopName in myShopMapRem.keys) {
                           if (myShopMapRem[shopName]!!.size>0)
                            Text(
                                text = "${shopName}",
                                modifier= Modifier
                                    .background(color = Color.LightGray)
                                    .padding(start = 5.dp, end = 15.dp)
                            )
                            for (myItem in myShopMap[shopName]!!) {
                                cartTotalValue += myItem.itemCartQuantity * myItem.itemprice.toDouble()
                                Row {
                                    Column(Modifier.weight(3f)) {
                                        Text(
                                            text = "${myItem.itemCartQuantity} x ${myItem.itemname}",
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            text = "${myItem.itemCartQuantity * myItem.itemprice.toDouble()}",
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                    Column(Modifier.weight(1f)) {
                                        IconButton(modifier = Modifier.
                                        then(Modifier.padding(8.dp).size(24.dp)),
                                            onClick = {removeCartItem (shopName,myItem)}) {
                                            Icon(
                                                Icons.Filled.Delete,
                                                "contentDescription",
                                                tint = Color.Blue)
                                        }
                                    }
                                    }
                                Divider()
                                }
                            Spacer(modifier = Modifier.padding (bottom=5.dp))
                            }
                            Spacer(modifier = Modifier.padding (bottom=5.dp))

                        Row{
                            Column(Modifier.weight(3f)) {
                                Text(
                                    text = "Total:",
                                    modifier = Modifier.padding(10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "${cartTotalRem.value}",
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding (bottom=5.dp))
                            Button(onClick = { showDeliveryAddressPage() }) {
                                Text("Enter Delivery Address")
                            }
                        }
                    }
                }
            }
        }
    }


