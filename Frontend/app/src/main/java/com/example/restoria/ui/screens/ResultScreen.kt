package com.example.restoria.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.restoria.ui.components.authBackground
import com.example.restoria.ui.theme.BackgroundBottom
import com.example.restoria.ui.theme.OutlineColor
import com.example.restoria.ui.theme.PrimaryButtonBrush
import com.example.restoria.ui.theme.RestoriaTheme
import com.example.restoria.ui.theme.SurfaceColor
import com.example.restoria.ui.theme.TextMuted
import com.example.restoria.ui.theme.TextPrimary
import kotlin.math.roundToInt

@Composable
fun ResultScreen(
    photoUri: Uri? = null,
    onBackClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    Box(
        Modifier
            .fillMaxSize()
            .authBackground()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.CenterStart)
                        .clip(CircleShape)
                        .background(SurfaceColor.copy(alpha = 0.8f))
                        .border(1.dp, OutlineColor, CircleShape)
                        .clickable(onClick = onBackClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Wstecz",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Wynik",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(Modifier.height(20.dp))

            CompareView(
                photoUri = photoUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(SurfaceColor.copy(alpha = 0.8f))
                        .border(1.dp, OutlineColor, RoundedCornerShape(26.dp))
                        .clickable(onClick = onRetryClick),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Outlined.Replay,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "  Odnów ponownie",
                        color = TextPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(PrimaryButtonBrush)
                        .clickable(onClick = onSaveClick),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Outlined.FileDownload,
                        contentDescription = null,
                        tint = BackgroundBottom,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "  Zapisz",
                        color = BackgroundBottom,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CompareView(photoUri: Uri?, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(28.dp)
    var split by remember { mutableFloatStateOf(0.5f) }
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .clip(shape)
            .border(1.dp, OutlineColor.copy(alpha = 0.6f), shape)
    ) {
        val widthPx = with(density) { maxWidth.toPx() }
        val handleSize = 44.dp
        val handlePx = with(density) { handleSize.toPx() }
        val linePx = with(density) { 1.dp.toPx() }

        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(widthPx) {
                    detectTapGestures { offset ->
                        split = (offset.x / widthPx).coerceIn(0.02f, 0.98f)
                    }
                }
                .pointerInput(widthPx) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        split = (change.position.x / widthPx).coerceIn(0.02f, 0.98f)
                    }
                }
        ) {
            PhotoLayer(
                photoUri = photoUri,
                restored = true,
                label = "po renowacji",
                modifier = Modifier.fillMaxSize()
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .clip(LeftPartShape(split))
            ) {
                PhotoLayer(
                    photoUri = photoUri,
                    restored = false,
                    label = "oryginał",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Pill("Przed", Modifier.align(Alignment.TopStart).padding(14.dp))
            Pill("Po", Modifier.align(Alignment.TopEnd).padding(14.dp))

            Box(
                Modifier
                    .offset { IntOffset((widthPx * split - linePx).roundToInt(), 0) }
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(Color.White)
            )

            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .offset { IntOffset((widthPx * split - handlePx / 2).roundToInt(), 0) }
                    .size(handleSize)
                    .shadow(6.dp, CircleShape)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.ChevronLeft,
                        contentDescription = null,
                        tint = BackgroundBottom,
                        modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = BackgroundBottom,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    "Przesuń, aby porównać",
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private class LeftPartShape(private val fraction: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = Outline.Rectangle(Rect(0f, 0f, size.width * fraction, size.height))
}

@Composable
private fun PhotoLayer(
    photoUri: Uri?,
    restored: Boolean,
    label: String,
    modifier: Modifier = Modifier
) {
    val gradient = if (restored) {
        Brush.verticalGradient(listOf(Color(0xFF2E7F87), Color(0xFF16505A)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF3B4C57), Color(0xFF1F2E38)))
    }
    Box(modifier.background(gradient), contentAlignment = Alignment.Center) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            if (restored) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0xFF2DD4BF).copy(alpha = 0.12f))
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Outlined.Image,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(26.dp)
                )
                Text("Zdjęcie 1", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                Text(label, color = TextMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun Pill(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.55f))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(text, color = TextPrimary, style = MaterialTheme.typography.labelSmall)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun ResultScreenPreview() {
    RestoriaTheme { ResultScreen() }
}