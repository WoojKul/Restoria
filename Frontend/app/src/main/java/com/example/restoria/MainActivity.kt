package com.example.restoria

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.restoria.ui.components.Tab
import com.example.restoria.ui.screens.CameraScreen
import com.example.restoria.ui.screens.GalleryScreen
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
                val context = LocalContext.current
                var screen by remember { mutableStateOf("splash") }

                val onTabClick: (Tab) -> Unit = { tab ->
                    when (tab) {
                        Tab.HOME -> screen = "home"
                        Tab.GALLERY -> screen = "gallery"
                        else -> {}
                    }
                }
                BackHandler(enabled = screen == "camera" || screen == "gallery") {
                    screen = "home"
                }

                when (screen) {
                    "splash" -> SplashScreen(onFinished = { screen = "home" })
                    "login" -> LoginScreen(onRegisterClick = { screen = "register" })
                    "register" -> RegisterScreen(
                        onBackClick = { screen = "login" },
                        onLoginClick = { screen = "login" }
                    )
                    "home" -> HomeScreen(
                        onPickPhotoClick = { screen = "gallery" },
                        onCameraClick = { screen = "camera" },
                        onTabClick = onTabClick,
                        onSeeAllClick = { screen = "gallery" }
                    )
                    "gallery" -> GalleryScreen(
                        onTabClick = onTabClick,
                        onCameraClick = { screen = "camera" },
                        onRestoreClick = { uris ->
                            Toast.makeText(context, "Wybrano zdjęć: ${uris.size}", Toast.LENGTH_SHORT).show()
                        }
                    )
                    "camera" -> CameraScreen(
                        onCloseClick = { screen = "home" }
                    )
                }
            }
        }
    }
}