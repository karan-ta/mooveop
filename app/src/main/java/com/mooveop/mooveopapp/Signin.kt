package com.mooveop.mooveopapp
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kodeplay.mooveopapp.ui.theme.MooveopAppTheme
import java.util.concurrent.TimeUnit

class Signin : ComponentActivity() {
    lateinit var phoneNumberText: MutableState<String>
     var enterOtpText = mutableStateOf ("")
    private lateinit var otpReceivedFromFirebase:String
    private val mAuth = FirebaseAuth.getInstance()
    var TAG = "mooveop"
    var inputMode = mutableStateOf ("enterPhoneNumber")
    fun checkUserLoggedIn ()
    {
        println ("inside checkUserLoggedIn")
        val user = Firebase.auth.currentUser
        if (user != null)
        {
//            val mainActivityIntent = Intent(this, MainActivity::class.java)
//            startActivity (mainActivityIntent)
//            val getRiderQuoteIntent = Intent(this, GetRiderQuoteActivity::class.java)
//            startActivity (getRiderQuoteIntent)

            val testIssueActivity = Intent(this, TestIssueActivity::class.java)
            startActivity (testIssueActivity)
        }
    }
    private fun otpVerification(otp: String) {
        val credential = PhoneAuthProvider.getCredential(otpReceivedFromFirebase, enterOtpText.value)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    println ("otp Verification Successful")
                    checkUserLoggedIn ()


                } else {
                    println ("Wrong Otp")
                }
            }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        lateinit private var resendToken:PhoneAuthProvider.ForceResendingToken

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(verificationId, token)
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            otpReceivedFromFirebase = verificationId
            resendToken = token
            inputMode.value = "enterOtp"
        }
    }

    fun sendPhoneNumberToFirebase (phoneNumberInput:String)
    {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumberInput)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //try logging out here to test if it all works ok and then comment it out.
//    Firebase.auth.signOut()

        checkUserLoggedIn ()
        setContent {
            println ("inside set Content")

            inputMode = remember {inputMode}
            phoneNumberText = remember { mutableStateOf("") }
            enterOtpText= remember {enterOtpText}

      if  (inputMode.value == "enterPhoneNumber") {
                Column {
                    OutlinedTextField(
                        value = phoneNumberText.value,
                        onValueChange = { phoneNumberText.value = it },
                        label = { Text("Enter Phone Number") },
                        modifier = Modifier.width(120.dp)
                    )
                    Button(onClick = { sendPhoneNumberToFirebase(phoneNumberText.value) }) {
                        Text(
                            text = "Verify Phone Number"
                        )
                    }
                }
            }
            else if (inputMode.value == "enterOtp")
            {
                Column {
                    OutlinedTextField(
                        value = enterOtpText.value,
                        onValueChange = { enterOtpText.value = it },
                        label = { Text("Enter OTP") },
                        modifier = Modifier.width(120.dp)
                    )
                    Button(onClick = { otpVerification(enterOtpText.value) }) {
                        Text(
                            text = "Enter OTP"
                        )
                    }
                }
            }
        }
    }
}

