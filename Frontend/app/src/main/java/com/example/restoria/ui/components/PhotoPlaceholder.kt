package com.example.restoria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.restoria.ui.theme.OutlineColor
import com.example.restoria.ui.theme.SurfaceVariantColor
import com.example.restoria.ui.theme.TextPrimary

@Composable
fun PhotoPlaceholder(label: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(SurfaceVariantColor.copy(alpha = 0.7f))
            .drawBehind {
                drawRoundRect(
                    color = OutlineColor,
                    cornerRadius = CornerRadius(22.dp.toPx()),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f))
                    )
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.Image, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(6.dp))
        Text(label, color = TextPrimary, style = MaterialTheme.typography.bodySmall)
    }
}