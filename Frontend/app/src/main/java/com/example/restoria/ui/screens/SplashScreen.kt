package com.example.restoria.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restoria.R
import com.example.restoria.ui.theme.BackgroundBottom
import com.example.restoria.ui.theme.Cream
import com.example.restoria.ui.theme.PrimaryButtonBrush
import com.example.restoria.ui.theme.RestoriaTheme
import com.example.restoria.ui.theme.TextMuted
import com.example.restoria.ui.theme.Turquoise
import kotlinx.coroutines.delay

// Kolory używane tylko na tym ekranie
private val SplashBase = Color(0xFF082630)
private val WarmGlow = Color(0xFF6B4A3F)
private val BlueGlow = Color(0xFF1D4F8C)
private val CardTealTop = Color(0xFF2E7F87)
private val CardTealBottom = Color(0xFF0F3E49)
private val CardGreyTop = Color(0xFF3B4C57)
private val CardGreyBottom = Color(0xFF1F2E38)

// Czas animacji
private const val CARD_3_DELAY = 200
private const val CARD_2_DELAY = 900
private const val CARD_1_DELAY = 1600
private const val LOGO_DELAY = 2300
private const val PROGRESS_DURATION = 4000

@Composable
fun SplashScreen(onFinished: () -> Unit = {}) {
    val inPreview = LocalInspectionMode.current
    val progress = remember { Animatable(if (inPreview) 0.6f else 0f) }
    LaunchedEffect(Unit) {
        if (!inPreview) {
            progress.animateTo(
                1f,
                tween(durationMillis = PROGRESS_DURATION, easing = FastOutSlowInEasing)
            )
            delay(300)
            onFinished()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SplashBase, BackgroundBottom)))
            .drawBehind {
                // Gradient w lewym górnym rogu
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(WarmGlow.copy(alpha = 0.55f), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = size.width * 0.9f
                    )
                )
                // Gradient po prawej
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(BlueGlow.copy(alpha = 0.45f), Color.Transparent),
                        center = Offset(size.width, size.height * 0.28f),
                        radius = size.width * 0.8f
                    )
                )
            }
    ) {
        val w = maxWidth

        // Karta 3
        Appear(
            delayMs = CARD_3_DELAY,
            modifier = Modifier
                .offset(x = w * 0.07f, y = w * 0.30f)
                .rotate(-6f)
        ) {
            PhotoCard(
                label = "Zdjęcie 3",
                width = w * 0.45f,
                height = w * 0.58f,
                topColor = CardGreyTop,
                bottomColor = CardGreyBottom
            )
        }

        // Karta 2
        Appear(
            delayMs = CARD_2_DELAY,
            modifier = Modifier
                .offset(x = w * 0.47f, y = w * 0.24f)
                .rotate(5f)
        ) {
            PhotoCard(
                label = "Zdjęcie 2",
                width = w * 0.48f,
                height = w * 0.62f,
                topColor = CardTealTop,
                bottomColor = CardTealBottom
            )
        }

        // Karta 1
        Appear(
            delayMs = CARD_1_DELAY,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = w * 0.74f)
                .rotate(-1f)
        ) {
            PhotoCard(
                label = "Zdjęcie 1",
                width = w * 0.44f,
                height = w * 0.57f,
                topColor = CardTealTop,
                bottomColor = CardTealBottom,
                fadeBottom = true
            )
        }

        // Logo, nazwa, podtytuł
        Appear(
            delayMs = LOGO_DELAY,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Gradient
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    listOf(
                                        Turquoise.copy(alpha = 0.40f),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.width * 1.1f
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_logo),
                        contentDescription = "Logo Restoria",
                        modifier = Modifier.size(140.dp)
                    )
                }
                Text(
                    text = "Restoria",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Cream,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 0.dp)
                )
                Text(
                    text = "Ożyw stare zdjęcia.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Pasek ładowania
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .width(104.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = progress.value
                        transformOrigin = TransformOrigin(0f, 0.5f)
                    }
                    .background(PrimaryButtonBrush)
            )
        }
    }
}

@Composable
private fun Appear(
    delayMs: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val inPreview = LocalInspectionMode.current
    var visible by remember { mutableStateOf(inPreview) }
    LaunchedEffect(Unit) {
        if (!inPreview) {
            delay(delayMs.toLong())
            visible = true
        }
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500),
        label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.9f,
        animationSpec = tween(500),
        label = "scale"
    )
    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha
            scaleX = scale
            scaleY = scale
        }
    ) {
        content()
    }
}

@Composable
private fun PhotoCard(
    label: String,
    width: Dp,
    height: Dp,
    topColor: Color,
    bottomColor: Color,
    fadeBottom: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .shadow(14.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Cream)
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.verticalGradient(listOf(topColor, bottomColor))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ImageIcon(Modifier.size(22.dp))
                Spacer(Modifier.height(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
        if (fadeBottom) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.55f to Color.Transparent,
                            1f to SplashBase.copy(alpha = 0.85f)
                        )
                    )
            )
        }
    }
}

@Composable
private fun ImageIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(
            width = size.width * 0.09f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
        val color = Color.White
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.08f, size.height * 0.08f),
            size = Size(size.width * 0.84f, size.height * 0.84f),
            cornerRadius = CornerRadius(size.width * 0.18f),
            style = stroke
        )
        drawCircle(
            color = color,
            radius = size.width * 0.08f,
            center = Offset(size.width * 0.34f, size.height * 0.34f)
        )
        val mountain = Path().apply {
            moveTo(size.width * 0.12f, size.height * 0.78f)
            lineTo(size.width * 0.40f, size.height * 0.52f)
            lineTo(size.width * 0.58f, size.height * 0.70f)
            lineTo(size.width * 0.70f, size.height * 0.58f)
            lineTo(size.width * 0.90f, size.height * 0.78f)
        }
        drawPath(mountain, color = color, style = stroke)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun SplashScreenPreview() {
    RestoriaTheme {
        SplashScreen()
    }
}