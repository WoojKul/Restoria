package com.example.restoria

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.restoria.ui.components.Tab
import com.example.restoria.ui.screens.CameraScreen
import com.example.restoria.ui.screens.GalleryScreen
import com.example.restoria.ui.screens.HomeScreen
import com.example.restoria.ui.screens.LoginScreen
import com.example.restoria.ui.screens.ProcessingScreen
import com.example.restoria.ui.screens.RegisterScreen
import com.example.restoria.ui.screens.RestorationOptionsScreen
import com.example.restoria.ui.screens.SplashScreen
import com.example.restoria.ui.screens.rememberFakeProgress
import com.example.restoria.ui.theme.RestoriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RestoriaTheme {
                var screen by remember { mutableStateOf("splash") }
                var selectedPhoto by remember { mutableStateOf<Uri?>(null) }

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
                BackHandler(enabled = screen == "options") {
                    screen = "gallery"
                }
                BackHandler(enabled = screen == "processing") {
                    screen = "options"
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
                            selectedPhoto = uris.firstOrNull()
                            screen = "options"
                        }
                    )
                    "camera" -> CameraScreen(
                        onCloseClick = { screen = "home" }
                    )
                    "options" -> RestorationOptionsScreen(
                        photoUri = selectedPhoto,
                        onBackClick = { screen = "gallery" },
                        onRestoreClick = { screen = "processing" }
                    )
                    "processing" -> {
                        val progress by rememberFakeProgress()
                        ProcessingScreen(
                            progress = progress,
                            photoUri = selectedPhoto,
                            onCancelClick = { screen = "options" },
                            onFinished = { screen = "home" }
                        )
                    }
                }
            }
        }
    }
}