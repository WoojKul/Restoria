package com.example.restoria.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.restoria.ui.theme.BackgroundBottom
import com.example.restoria.ui.theme.RestoriaTheme
import com.example.restoria.ui.theme.SurfaceColor
import com.example.restoria.ui.theme.TextMuted
import com.example.restoria.ui.theme.TextPrimary
import com.example.restoria.ui.theme.Turquoise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CameraScreen(
    onCloseClick: () -> Unit = {},
    onShutterClick: () -> Unit = {},
    onFlashClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    onImagePicked: (Uri) -> Unit = {},
    lastPhotoUri: Uri? = null,
    onLastPhotoClick: (Uri) -> Unit = {}
) {
    val context = LocalContext.current
    val inPreview = LocalInspectionMode.current

    var hasPermission by remember {
        mutableStateOf(
            inPreview || ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) launcher.launch(Manifest.permission.CAMERA)
    }

    var camera by remember { mutableStateOf<Camera?>(null) }
    var flashOn by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) onImagePicked(uri)
    }

    val lastThumb by produceState<ImageBitmap?>(null, lastPhotoUri) {
        value = lastPhotoUri?.let { uri ->
            withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        BitmapFactory.decodeStream(
                            input, null,
                            BitmapFactory.Options().apply { inSampleSize = 4 }
                        )
                    }?.asImageBitmap()
                }.getOrNull()
            }
        }
    }

    LaunchedEffect(camera, flashOn) {
        camera?.let {
            if (it.cameraInfo.hasFlashUnit()) {
                it.cameraControl.enableTorch(flashOn)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBottom)
    ) {
        if (hasPermission && !inPreview) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onCameraReady = { camera = it }
            )
        } else {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (!inPreview) {
                    Text(
                        "Brak zgody na aparat",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        CornerFrame(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
                .padding(vertical = 0.dp)
                .size(width = 360.dp, height = 480.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundIconButton(Icons.Outlined.Close, "Zamknij", onCloseClick)
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Turquoise)
                    )
                    Text(
                        "  Ułóż zdjęcie w ramce",
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            RoundIconButton(
                icon = if (flashOn) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                description = "Błysk",
                onClick = {
                    flashOn = !flashOn
                    onFlashClick()
                },
                active = flashOn
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceColor)
                    .border(1.dp, Turquoise.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .clickable(enabled = lastPhotoUri != null) {
                        lastPhotoUri?.let(onLastPhotoClick)
                    },
                contentAlignment = Alignment.Center
            ) {
                val thumb = lastThumb
                if (thumb != null) {
                    Image(
                        bitmap = thumb,
                        contentDescription = "Ostatnie zdjęcie",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Outlined.Image,
                        contentDescription = "Brak zdjęć",
                        tint = TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                ShutterButton(onShutterClick)
            }
            RoundIconButton(
                icon = Icons.Outlined.PhotoLibrary,
                description = "Galeria",
                onClick = {
                    onGalleryClick()
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }
    }
}

@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    onCameraReady: (Camera) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            val providerFuture = ProcessCameraProvider.getInstance(ctx)
            providerFuture.addListener({
                val provider = providerFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                try {
                    provider.unbindAll()
                    val cam = provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview
                    )
                    onCameraReady(cam)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
            previewView
        }
    )
}

@Composable
private fun RoundIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit,
    active: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                if (active) Turquoise.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.45f)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = description, tint = TextPrimary, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun ShutterButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(78.dp)
            .border(4.dp, Color.White, CircleShape)
            .padding(8.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick)
    )
}

@Composable
private fun CornerFrame(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.drawBehind {
            val len = 36.dp.toPx()
            val stroke = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            val w = size.width
            val h = size.height
            fun corner(x: Float, y: Float, dx: Float, dy: Float) {
                drawLine(Turquoise, Offset(x, y), Offset(x + dx * len, y), stroke.width, StrokeCap.Round)
                drawLine(Turquoise, Offset(x, y), Offset(x, y + dy * len), stroke.width, StrokeCap.Round)
            }
            corner(0f, 0f, 1f, 1f)
            corner(w, 0f, -1f, 1f)
            corner(0f, h, 1f, -1f)
            corner(w, h, -1f, -1f)
        }
    )
}

@ComposePreview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun CameraScreenPreview() {
    RestoriaTheme { CameraScreen() }
}