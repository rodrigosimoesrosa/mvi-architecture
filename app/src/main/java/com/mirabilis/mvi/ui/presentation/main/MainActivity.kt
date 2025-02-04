package com.mirabilis.mvi.ui.presentation.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mirabilis.mvi.ui.presentation.main.signup.SignUpScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            /*SignInScreen(
                onNext = {
                    //TODO go to another screen
                    Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_LONG).show()
                }
            )*/

            SignUpScreen(
                onNext = {
                    //TODO go to another screen
                    Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}
