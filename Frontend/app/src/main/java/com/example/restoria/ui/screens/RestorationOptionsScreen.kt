package com.example.restoria.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.exifinterface.media.ExifInterface
import coil.compose.AsyncImage
import com.example.restoria.ui.components.SettingCard
import com.example.restoria.ui.components.SettingSlider
import com.example.restoria.ui.components.SettingSwitch
import com.example.restoria.ui.components.authBackground
import com.example.restoria.ui.theme.BackgroundBottom
import com.example.restoria.ui.theme.OutlineColor
import com.example.restoria.ui.theme.PrimaryButtonBrush
import com.example.restoria.ui.theme.RestoriaTheme
import com.example.restoria.ui.theme.SurfaceColor
import com.example.restoria.ui.theme.TextMuted
import com.example.restoria.ui.theme.TextPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun RestorationOptionsScreen(
    photoUri: Uri? = null,
    onBackClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    onRestoreClick: () -> Unit = {}
) {
    var denoise by remember { mutableFloatStateOf(75f) }
    var scratches by remember { mutableStateOf(true) }
    var sharpness by remember { mutableFloatStateOf(50f) }
    var contrast by remember { mutableFloatStateOf(50f) }
    var colorize by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .authBackground()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 120.dp)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                RoundButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Wstecz",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Opcje renowacji",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
                RoundButton(
                    onClick = onInfoClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = "Informacje",
                        tint = TextPrimary
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            OriginalPhotoCard(photoUri)

            Spacer(Modifier.height(18.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingCard(
                    title = "Usuwanie szumów",
                    subtitle = "Redukcja ziarna i szumu",
                    valueText = "${denoise.toInt()}%",
                    bottom = {
                        SettingSlider(
                            value = denoise,
                            onValueChange = { denoise = it },
                            step = 25
                        )
                    }
                )
                SettingCard(
                    title = "Naprawa rys i plam",
                    subtitle = "Wykrywa i usuwa uszkodzenia",
                    trailing = {
                        SettingSwitch(checked = scratches, onCheckedChange = { scratches = it })
                    }
                )
                SettingCard(
                    title = "Poprawa ostrości",
                    subtitle = "Wyostrza rozmyte szczegóły",
                    valueText = "${sharpness.toInt()}%",
                    bottom = {
                        SettingSlider(
                            value = sharpness,
                            onValueChange = { sharpness = it }
                        )
                    }
                )
                SettingCard(
                    title = "Poprawa kontrastu",
                    subtitle = "Pogłębia światła i cienie",
                    valueText = "${contrast.toInt()}%",
                    bottom = {
                        SettingSlider(
                            value = contrast,
                            onValueChange = { contrast = it }
                        )
                    }
                )
                SettingCard(
                    title = "Koloryzacja",
                    subtitle = "Dodaje kolory do zdjęć czarno-białych",
                    trailing = {
                        SettingSwitch(checked = colorize, onCheckedChange = { colorize = it })
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(PrimaryButtonBrush)
                .clickable(onClick = onRestoreClick),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "  Odnów zdjęcie",
                    color = BackgroundBottom,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun RoundButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(SurfaceColor.copy(alpha = 0.8f))
            .border(1.dp, OutlineColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun OriginalPhotoCard(photoUri: Uri?) {
    val shape = RoundedCornerShape(24.dp)
    val context = LocalContext.current
    var sizeText by remember(photoUri) { mutableStateOf<String?>(null) }
    LaunchedEffect(photoUri) {
        sizeText = if (photoUri != null) readImageSize(context, photoUri) else null
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.25f))
            .drawBehind {
                drawRoundRect(
                    color = TextMuted.copy(alpha = 0.7f),
                    cornerRadius = CornerRadius(24.dp.toPx()),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f))
                    )
                )
            }
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = "Wybrane zdjęcie",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
            )
        } else {
            Column(
                Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.Image,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text("Zdjęcie", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
            }
        }
        Pill("Oryginał", Modifier.align(Alignment.TopStart).padding(12.dp))
        Pill(sizeText ?: "1536 × 2048", Modifier.align(Alignment.TopEnd).padding(12.dp))
    }
}

@Composable
private fun Pill(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.55f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, color = TextPrimary, style = MaterialTheme.typography.labelSmall)
    }
}

private suspend fun readImageSize(context: Context, uri: Uri): String? =
    withContext(Dispatchers.IO) {
        try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }
            var w = options.outWidth
            var h = options.outHeight
            if (w <= 0 || h <= 0) return@withContext null

            val rotation = context.contentResolver.openInputStream(uri)?.use {
                ExifInterface(it).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            }
            if (rotation == ExifInterface.ORIENTATION_ROTATE_90 ||
                rotation == ExifInterface.ORIENTATION_ROTATE_270 ||
                rotation == ExifInterface.ORIENTATION_TRANSPOSE ||
                rotation == ExifInterface.ORIENTATION_TRANSVERSE
            ) {
                val t = w
                w = h
                h = t
            }
            "$w × $h"
        } catch (e: Exception) {
            null
        }
    }

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun RestorationOptionsPreview() {
    RestoriaTheme { RestorationOptionsScreen() }
}