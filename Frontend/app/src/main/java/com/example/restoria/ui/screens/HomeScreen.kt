package com.example.restoria.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.restoria.ui.components.BeforeAfterCard
import com.example.restoria.ui.components.PhotoPlaceholder
import com.example.restoria.ui.components.RestoriaBottomBar
import com.example.restoria.ui.components.Tab
import com.example.restoria.ui.components.ToolItem
import com.example.restoria.ui.theme.BackgroundBrush
import com.example.restoria.ui.theme.Blue
import com.example.restoria.ui.theme.Orange
import com.example.restoria.ui.theme.Success
import com.example.restoria.ui.theme.TextMuted
import com.example.restoria.ui.theme.TextPrimary
import com.example.restoria.ui.theme.Turquoise

@Composable
fun HomeScreen(
    onPickPhotoClick: () -> Unit,
    onCameraClick: () -> Unit,
    onTabClick: (Tab) -> Unit,
    onToolClick: (String) -> Unit = {},
    onSeeAllClick: () -> Unit = {}
) {
    Box(Modifier.fillMaxSize().background(BackgroundBrush)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 96.dp)
        ) {
            Spacer(Modifier.height(48.dp))
            Text(
                text = "Dzień dobry",
                color = TextMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Co dziś odnawiamy?",
                color = TextPrimary,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(20.dp))
            BeforeAfterCard(onPickClick = onPickPhotoClick)

            Spacer(Modifier.height(30.dp))
            Text(
                text = "Narzędzia",
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ToolItem(Icons.Outlined.AutoAwesome, "Renowacja", Turquoise, { onToolClick("renovation") }, Modifier.weight(1f))
                ToolItem(Icons.Outlined.Tune, "Usuń szum", Orange, { onToolClick("denoise") }, Modifier.weight(1f))
                ToolItem(Icons.Outlined.Palette, "Koloryzacja", Blue, { onToolClick("colorize") }, Modifier.weight(1f))
                ToolItem(Icons.Outlined.Link, "Rysy i plamy", Success, { onToolClick("scratches") }, Modifier.weight(1f))
            }

            Spacer(Modifier.height(30.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ostatnio odnowione",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Zobacz wszystkie",
                    color = Turquoise,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable(onClick = onSeeAllClick)
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(3) { i ->
                    PhotoPlaceholder("Zdjęcie ${i + 1}", Modifier.weight(1f).height(130.dp))
                }
            }
        }

        RestoriaBottomBar(
            selected = Tab.HOME,
            onTabClick = onTabClick,
            onCameraClick = onCameraClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun HomeScreenPreview() {
    com.example.restoria.ui.theme.RestoriaTheme {
        HomeScreen(
            onPickPhotoClick = {},
            onCameraClick = {},
            onTabClick = {}
        )
    }
}