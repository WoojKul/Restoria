package com.example.restoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.restoria.ui.screens.LoginScreen
import com.example.restoria.ui.screens.RegisterScreen
import com.example.restoria.ui.theme.RestoriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RestoriaTheme {
                var showRegister by remember { mutableStateOf(false) }
                if (showRegister) {
                    RegisterScreen(
                        onBackClick = { showRegister = false },
                        onLoginClick = { showRegister = false }
                    )
                } else {
                    LoginScreen(onRegisterClick = { showRegister = true })
                }
            }
        }
    }
}