package com.example.restoria.ui.components

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.restoria.ui.theme.BackgroundBottom

private val AuthBase = Color(0xFF0A2A35)
private val WarmGlow = Color(0xFF6B4A3F)
private val BlueGlow = Color(0xFF1D4F8C)
private val TealGlow = Color(0xFF14606B)

fun Modifier.authBackground(): Modifier = this
    .background(Brush.verticalGradient(listOf(AuthBase, BackgroundBottom)))
    .drawBehind {
        drawRect(
            Brush.radialGradient(
                colors = listOf(WarmGlow.copy(alpha = 0.55f), Color.Transparent),
                center = Offset(0f, 0f),
                radius = size.width * 0.9f
            )
        )
        drawRect(
            Brush.radialGradient(
                colors = listOf(BlueGlow.copy(alpha = 0.40f), Color.Transparent),
                center = Offset(size.width, size.height * 0.30f),
                radius = size.width * 0.85f
            )
        )
        drawRect(
            Brush.radialGradient(
                colors = listOf(TealGlow.copy(alpha = 0.30f), Color.Transparent),
                center = Offset(size.width * 0.4f, size.height * 0.55f),
                radius = size.width * 0.9f
            )
        )
    }