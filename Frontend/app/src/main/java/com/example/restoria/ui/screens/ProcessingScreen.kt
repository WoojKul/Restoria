package com.example.restoria.ui.screens

import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.restoria.ui.components.authBackground
import com.example.restoria.ui.theme.BackgroundBottom
import com.example.restoria.ui.theme.Cream
import com.example.restoria.ui.theme.OutlineColor
import com.example.restoria.ui.theme.PrimaryButtonBrush
import com.example.restoria.ui.theme.RestoriaTheme
import com.example.restoria.ui.theme.SurfaceVariantColor
import com.example.restoria.ui.theme.TextMuted
import com.example.restoria.ui.theme.TextPrimary
import com.example.restoria.ui.theme.Turquoise
import kotlin.math.abs

const val FAKE_PROCESSING_MS = 20_000

@Composable
fun rememberFakeProgress(durationMs: Int = FAKE_PROCESSING_MS): State<Float> {
    val inPreview = LocalInspectionMode.current
    val value = remember { mutableFloatStateOf(if (inPreview) 0.64f else 0f) }
    LaunchedEffect(durationMs) {
        if (inPreview) return@LaunchedEffect
        val anim = Animatable(0f)
        anim.animateTo(1f, tween(durationMs, easing = LinearEasing)) {
            value.floatValue = this.value
        }
    }
    return value
}

private val Stages = listOf("Szum", "Rysy", "Kolor")
private val StageSubtitles = listOf(
    "Usuwanie szumów",
    "Naprawa rys i plam",
    "Koloryzacja"
)

private val SmoothEasing = CubicBezierEasing(0.65f, 0.0f, 0.35f, 1.0f)

@Composable
fun ProcessingScreen(
    progress: Float,
    totalSeconds: Int = FAKE_PROCESSING_MS / 1000,
    photoUri: Uri? = null,
    onCancelClick: () -> Unit = {},
    onFinished: () -> Unit = {}
) {
    val p = progress.coerceIn(0f, 1f)
    val remaining = ((1f - p) * totalSeconds).toInt().coerceAtLeast(0)

    val stage = when {
        p < 1f / 3f -> 0
        p < 2f / 3f -> 1
        else -> 2
    }

    val inPreview = LocalInspectionMode.current
    LaunchedEffect(p >= 1f) {
        if (p >= 1f && !inPreview) onFinished()
    }

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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            ScanFrame(
                photoUri = photoUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Odnawiamy zdjęcie",
                color = Cream,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = StageSubtitles[stage],
                color = TextMuted,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(20.dp))
            ProgressBar(progress = p)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Zostało ok. $remaining s",
                    color = TextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${(p * 100).toInt()}%",
                    color = Turquoise,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Stages.forEachIndexed { index, label ->
                    StageChip(
                        label = label,
                        state = when {
                            index < stage -> StageState.DONE
                            index == stage -> StageState.ACTIVE
                            else -> StageState.PENDING
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = "Anuluj",
                color = TextMuted,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .clickable(onClick = onCancelClick)
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ScanFrame(photoUri: Uri?, modifier: Modifier = Modifier) {
    val inPreview = LocalInspectionMode.current
    val transition = rememberInfiniteTransition(label = "scan")

    val scan by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = SmoothEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val line = if (inPreview) 0.5f else scan

    var previous by remember { mutableFloatStateOf(0f) }
    var goingDown by remember { mutableStateOf(true) }
    var speed by remember { mutableFloatStateOf(0f) }

    if (line != previous) {
        val delta = line - previous
        goingDown = delta > 0f
        val instant = abs(delta) * 60f
        speed += (instant - speed) * 0.2f
        previous = line
    }

    val maxSpeed = 1.1f
    val drag = (speed / maxSpeed).coerceIn(0f, 1f)

    val direction by animateFloatAsState(
        targetValue = if (goingDown) 1f else 0f,
        animationSpec = tween(250),
        label = "direction"
    )

    val shape = RoundedCornerShape(28.dp)

    BoxWithConstraints(modifier) {
        val inset = 14.dp
        val innerH = maxHeight - inset * 2

        Box(
            Modifier
                .fillMaxSize()
                .padding(inset)
                .clip(shape)
                .border(1.dp, OutlineColor.copy(alpha = 0.6f), shape)
        ) {
            PhotoPanel(photoUri = photoUri, modifier = Modifier.fillMaxSize())

            Canvas(Modifier.fillMaxSize()) {
                val y = size.height * line

                val tail = size.height * (0.06f + 0.49f * drag)
                val tailAlpha = 0.10f + 0.40f * drag

                val aboveStart = (y - tail).coerceAtLeast(0f)
                if (y > aboveStart) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Turquoise.copy(alpha = tailAlpha * 0.25f * direction),
                                Turquoise.copy(alpha = tailAlpha * direction)
                            ),
                            startY = aboveStart,
                            endY = y
                        ),
                        topLeft = Offset(0f, aboveStart),
                        size = Size(size.width, y - aboveStart)
                    )
                }

                val belowEnd = (y + tail).coerceAtMost(size.height)
                if (belowEnd > y) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Turquoise.copy(alpha = tailAlpha * (1f - direction)),
                                Turquoise.copy(alpha = tailAlpha * 0.25f * (1f - direction)),
                                Color.Transparent
                            ),
                            startY = y,
                            endY = belowEnd
                        ),
                        topLeft = Offset(0f, y),
                        size = Size(size.width, belowEnd - y)
                    )
                }

                val halo = 22.dp.toPx()
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Turquoise.copy(alpha = 0.22f + 0.13f * drag),
                            Color.Transparent
                        ),
                        startY = y - halo,
                        endY = y + halo
                    ),
                    topLeft = Offset(0f, y - halo),
                    size = Size(size.width, halo * 2)
                )
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .offset(y = innerH * line + inset - 6.dp)
        ) {
            Box(
                Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.Center)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Turquoise.copy(alpha = 0.3f),
                                Turquoise,
                                Color.White.copy(alpha = 0.9f),
                                Turquoise,
                                Turquoise.copy(alpha = 0.3f)
                            )
                        ),
                        RoundedCornerShape(2.dp)
                    )
            )
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .size(12.dp)
                    .background(Turquoise, CircleShape)
            )
            Box(
                Modifier
                    .align(Alignment.CenterEnd)
                    .size(12.dp)
                    .background(Turquoise, CircleShape)
            )
        }

        Canvas(Modifier.fillMaxSize()) {
            val len = 34.dp.toPx()
            val w = 4.dp.toPx()
            val s = size.width
            val t = size.height
            val color = Turquoise.copy(alpha = if (inPreview) 1f else pulse)
            fun corner(x: Float, y: Float, dx: Float, dy: Float) {
                drawLine(color, Offset(x, y), Offset(x + dx * len, y), w, StrokeCap.Round)
                drawLine(color, Offset(x, y), Offset(x, y + dy * len), w, StrokeCap.Round)
            }
            corner(w / 2, w / 2, 1f, 1f)
            corner(s - w / 2, w / 2, -1f, 1f)
            corner(w / 2, t - w / 2, 1f, -1f)
            corner(s - w / 2, t - w / 2, -1f, -1f)
        }
    }
}

@Composable
private fun PhotoPanel(photoUri: Uri?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(listOf(Color(0xFF3B4C57), Color(0xFF1F2E38)))
        ),
        contentAlignment = Alignment.Center
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Outlined.Image,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text("Zdjęcie 1", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun ProgressBar(progress: Float) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color.White.copy(alpha = 0.12f))
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .background(PrimaryButtonBrush)
        )
    }
}

private enum class StageState { DONE, ACTIVE, PENDING }

@Composable
private fun StageChip(label: String, state: StageState) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(SurfaceVariantColor.copy(alpha = 0.6f))
            .border(1.dp, OutlineColor.copy(alpha = 0.7f), CircleShape)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(18.dp)
                .clip(CircleShape)
                .then(
                    when (state) {
                        StageState.DONE -> Modifier.background(Turquoise)
                        StageState.ACTIVE -> Modifier.border(2.dp, Turquoise, CircleShape)
                        StageState.PENDING -> Modifier.border(2.dp, TextMuted.copy(alpha = 0.6f), CircleShape)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                StageState.DONE -> Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = BackgroundBottom,
                    modifier = Modifier.size(12.dp)
                )
                StageState.ACTIVE -> Box(
                    Modifier
                        .size(7.dp)
                        .background(Turquoise, CircleShape)
                )
                StageState.PENDING -> {}
            }
        }
        Text(
            text = "  $label",
            color = if (state == StageState.PENDING) TextMuted else TextPrimary,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun ProcessingScreenPreview() {
    RestoriaTheme { ProcessingScreen(progress = 0.64f) }
}