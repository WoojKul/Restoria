package com.example.restoria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.restoria.ui.theme.OutlineColor
import com.example.restoria.ui.theme.SurfaceVariantColor
import com.example.restoria.ui.theme.TextMuted
import com.example.restoria.ui.theme.TextPrimary
import com.example.restoria.ui.theme.Turquoise

@Composable
fun SettingCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    valueText: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    bottom: @Composable (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceVariantColor.copy(alpha = 0.55f))
            .border(1.dp, OutlineColor.copy(alpha = 0.6f), shape)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, color = TextMuted, style = MaterialTheme.typography.bodySmall)
            }
            if (valueText != null) {
                Text(valueText, color = Turquoise, style = MaterialTheme.typography.labelLarge)
            }
            if (trailing != null) trailing()
        }
        if (bottom != null) bottom()
    }
}