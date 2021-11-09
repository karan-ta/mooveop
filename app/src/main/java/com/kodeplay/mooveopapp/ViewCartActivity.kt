package com.kodeplay.mooveopapp
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kodeplay.mooveopapp.ui.theme.MooveopAppTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ViewCartActivity : ComponentActivity() {
//    var mPrefs = getPreferences(MODE_PRIVATE)
//    var myShopMap = mutableMapOf <String,MutableList<CartItem>>()
fun showDeliveryAddressPage()
{
    val deliveryIntent = Intent(this, DeliveryActivity::class.java)
    startActivity (deliveryIntent)
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gson = Gson()
    val sharedPreferences = getSharedPreferences("com.kodeplay.mooveopapp.prefs", MODE_PRIVATE)
    val json = sharedPreferences.getString("myShopMap", "")
        val type: Type = object : TypeToken<MutableMap<String,MutableList<CartItem>>>() {}.type
        val myShopMap:MutableMap<String,MutableList<CartItem>>
          =  Gson().fromJson<MutableMap<String,MutableList<CartItem>>>(json, type)
    println (myShopMap.keys)
    println (myShopMap.values)
        setContent {
            LazyColumn {
                item {
                    Text(

                        text = "Your Order",
                        style = MaterialTheme.typography.h4,
                    )
                    for (shopName in myShopMap.keys) {
                        Text(
                            text = "${shopName}",
                        )
                        for (myItem in myShopMap[shopName]!!) {
                            Text("${myItem.itemCartQuantity} x ${myItem.itemname}")
                            Divider()
                        }
                    }
                    Button(onClick = { showDeliveryAddressPage ()}) {

                    }
                }
            }
        }
    }
}

