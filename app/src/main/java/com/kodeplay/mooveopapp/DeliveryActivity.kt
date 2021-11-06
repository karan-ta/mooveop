package com.kodeplay.mooveopapp
import android.app.Activity
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
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.nio.charset.Charset
data class RazorpayOrder(
    val amount:Int,
    val amount_paid:Int,
    val notes:Array<String>,
    val created_at:Int,
    val amount_due:Int,
    var currency:String,
    var receipt:String,
    var id:String,
    var entity:String,
    var offer_id:Any,
    var status:String,
    var attempts:Int

)
class DeliveryActivity : ComponentActivity(), PaymentResultListener {
    lateinit var isGateDelivery:MutableState<Boolean>
    lateinit var isFlatDelivery:MutableState<Boolean>
    lateinit var phoneNumberText:MutableState<String>
    lateinit var flatNumberText:MutableState<String>
    lateinit var buildingNameText:MutableState<String>
    lateinit var landmarkText:MutableState<String>

    private fun startPayment() {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()
        try {
            val options = JSONObject()
            options.put("name","Razorpay Corp")
            options.put("description","Demoing Charges")
            //You can omit the image option to fetch the image from dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("theme.color", "#3399cc");
            options.put("currency","INR");
            options.put("order_id", "order_DBJOWzybf0sJbb");
            options.put("amount","50000")//pass amount in currency subunits

            val retryObj =  JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email","gaurav.kumar@example.com")
            prefill.put("contact","9876543210")

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            println (e.message)
            e.printStackTrace()
        }
    }

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
        else {
            errorString.value = ""
            val queue = Volley.newRequestQueue(this)
            val url = "https://mooveop.herokuapp.com/razorpaytesting"
            val requestBody = "amount=3000&deliveryType=flat&landmark="+landmarkText.value+"&buildingName="+buildingNameText.value+"&flatNumber="+flatNumberText.value
            val stringReq : StringRequest =
                object : StringRequest(Method.POST, url,
                    Response.Listener { response ->
                        // response
                        var strResp = response.toString()
                        println ("created order")
                        println (response)
                        println (strResp)
                        var razorpayOrderData = Gson ().fromJson(
                            response,
                            RazorpayOrder::class.java
                        )
                        println (razorpayOrderData.id)
                        startPayment ()
                    },
                    Response.ErrorListener { error ->
                    }
                ){
                    override fun getBody(): ByteArray {
                        return requestBody.toByteArray(Charset.defaultCharset())
                    }
                }
            queue.add(stringReq)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Checkout().setKeyID("rzp_test_MAVdJtlc3h9K7x")
        setContent {

            Checkout.preload(applicationContext)
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

    override fun onPaymentSuccess(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        TODO("Not yet implemented")
    }
}
