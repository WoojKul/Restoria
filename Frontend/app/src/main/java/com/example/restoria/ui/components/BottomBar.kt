package com.example.restoria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.restoria.ui.theme.PrimaryButtonBrush
import com.example.restoria.ui.theme.SurfaceColor
import com.example.restoria.ui.theme.TextMuted
import com.example.restoria.ui.theme.TextPrimary
import com.example.restoria.ui.theme.Turquoise

enum class Tab { HOME, GALLERY, HISTORY, PROFILE }

@Composable
fun RestoriaBottomBar(
    selected: Tab,
    onTabClick: (Tab) -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxWidth().height(82.dp)) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(SurfaceColor.copy(alpha = 0.95f)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(Icons.Outlined.Home, "Główna", selected == Tab.HOME, Modifier.weight(1f)) { onTabClick(Tab.HOME) }
            NavItem(Icons.Outlined.PhotoLibrary, "Galeria", selected == Tab.GALLERY, Modifier.weight(1f)) { onTabClick(Tab.GALLERY) }
            Spacer(Modifier.weight(1f))
            NavItem(Icons.Outlined.History, "Historia", selected == Tab.HISTORY, Modifier.weight(1f)) { onTabClick(Tab.HISTORY) }
            NavItem(Icons.Outlined.Person, "Profil", selected == Tab.PROFILE, Modifier.weight(1f)) { onTabClick(Tab.PROFILE) }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(68.dp)
                .clip(CircleShape)
                .background(SurfaceColor),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(PrimaryButtonBrush)
                    .clickable(onClick = onCameraClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.PhotoCamera,
                    contentDescription = "Aparat",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    active: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val color = if (active) Turquoise else TextMuted
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .clip(CircleShape)
                .background(if (active) Turquoise.copy(alpha = 0.18f) else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
        }
        Text(label, color = color, style = MaterialTheme.typography.labelSmall)
    }
}