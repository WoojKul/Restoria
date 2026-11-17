package com.example.restoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.restoria.ui.screens.CameraScreen
import com.example.restoria.ui.screens.HomeScreen
import com.example.restoria.ui.screens.LoginScreen
import com.example.restoria.ui.screens.RegisterScreen
import com.example.restoria.ui.screens.SplashScreen
import com.example.restoria.ui.theme.RestoriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RestoriaTheme {
                var screen by remember { mutableStateOf("splash") }

                BackHandler(enabled = screen == "camera") { screen = "home" }

                when (screen) {
                    "splash" -> SplashScreen(onFinished = { screen = "home" })
                    "login" -> LoginScreen(onRegisterClick = { screen = "register" })
                    "register" -> RegisterScreen(
                        onBackClick = { screen = "login" },
                        onLoginClick = { screen = "login" }
                    )
                    "home" -> HomeScreen(
                        onPickPhotoClick = {},
                        onCameraClick = { screen = "camera" },
                        onTabClick = {}
                    )
                    "camera" -> CameraScreen(
                        onCloseClick = { screen = "home" }
                    )
                }
            }
        }
    }
}