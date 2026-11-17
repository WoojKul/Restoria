package com.example.restoria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.restoria.ui.theme.OutlineColor
import com.example.restoria.ui.theme.PrimaryButtonBrush
import com.example.restoria.ui.theme.SurfaceColor
import com.example.restoria.ui.theme.SurfaceVariantColor
import com.example.restoria.ui.theme.TextMuted
import com.example.restoria.ui.theme.TextPrimary

@Composable
fun BeforeAfterCard(onPickClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(28.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(shape)
            .border(1.dp, OutlineColor, shape)
    ) {
        Row(Modifier.fillMaxSize()) {
            Half("Przed", "Zdjęcie 2", "oryginał", SurfaceColor, Modifier.weight(1f))
            Half("Po", "Zdjęcie 2", "po renowacji", SurfaceVariantColor, Modifier.weight(1f))
        }
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = "Odnów swoje zdjęcie",
                color = TextPrimary,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PrimaryButtonBrush)
                    .clickable(onClick = onPickClick)
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Wybierz zdjęcie",
                    color = TextPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun Half(tag: String, title: String, sub: String, bg: Color, modifier: Modifier) {
    Box(modifier.fillMaxHeight().background(bg)) {
        Box(
            Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(tag, color = TextPrimary, style = MaterialTheme.typography.labelSmall)
        }
        Column(
            Modifier
                .align(Alignment.Center)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Outlined.Image, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
            Text(title, color = TextPrimary, style = MaterialTheme.typography.labelLarge)
            Text(sub, color = TextMuted, style = MaterialTheme.typography.labelSmall)
        }
    }
}