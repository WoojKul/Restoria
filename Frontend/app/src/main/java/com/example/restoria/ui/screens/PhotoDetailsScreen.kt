package com.example.restoria.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.restoria.ui.components.SettingSwitch
import com.example.restoria.ui.components.authBackground
import com.example.restoria.ui.theme.BackgroundBottom
import com.example.restoria.ui.theme.Cream
import com.example.restoria.ui.theme.OutlineColor
import com.example.restoria.ui.theme.PrimaryButtonBrush
import com.example.restoria.ui.theme.RestoriaTheme
import com.example.restoria.ui.theme.SurfaceColor
import com.example.restoria.ui.theme.SurfaceVariantColor
import com.example.restoria.ui.theme.TextMuted
import com.example.restoria.ui.theme.TextPrimary
import com.example.restoria.ui.theme.Turquoise

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhotoDetailsScreen(
    photoUri: Uri? = null,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("Zdjęcie przykładowe") }
    var year by remember { mutableStateOf("1952") }
    var place by remember { mutableStateOf("Gliwice") }
    val people = remember { mutableStateListOf("Jan Kowalski") }
    var newPerson by remember { mutableStateOf("") }
    var addingPerson by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("Zdjęcie przykładowe.") }
    var captionOn by remember { mutableStateOf(true) }

    val caption = buildList {
        if (people.isNotEmpty()) add(people.joinToString(", "))
        if (place.isNotBlank()) add(place.trim())
    }.joinToString(", ").let { base ->
        listOf(base, year.trim()).filter { it.isNotBlank() }.joinToString(" ")
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
                .imePadding()
                .verticalScroll(rememberScrollState())
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
                    text = "Dane zdjęcia",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Thumbnail(photoUri)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Uzupełnij dane",
                        color = Cream,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Na ich podstawie powstanie\npodpis zdjęcia.",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(Turquoise.copy(alpha = 0.18f))
                            .border(1.dp, Turquoise.copy(alpha = 0.5f), CircleShape)
                            .padding(horizontal = 14.dp, vertical = 5.dp)
                    ) {
                        Text("Odnowione", color = Turquoise, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            FieldLabel("Nazwa")
            FormField(
                value = name,
                onValueChange = { name = it },
                icon = Icons.Outlined.Edit,
                highlighted = true,
                bold = true
            )

            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(0.8f)) {
                    FieldLabel("Rok")
                    FormField(
                        value = year,
                        onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) year = it },
                        icon = Icons.Outlined.CalendarToday,
                        keyboardType = KeyboardType.Number
                    )
                }
                Column(Modifier.weight(1.2f)) {
                    FieldLabel("Miejsce")
                    FormField(
                        value = place,
                        onValueChange = { place = it },
                        icon = Icons.Outlined.LocationOn
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            FieldLabel("Osoby na zdjęciu")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceVariantColor.copy(alpha = 0.55f))
                    .border(1.dp, OutlineColor.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Group,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    people.forEach { person ->
                        PersonChip(person, onRemove = { people.remove(person) })
                    }
                    if (addingPerson) {
                        AddPersonField(
                            value = newPerson,
                            onValueChange = { newPerson = it },
                            onDone = {
                                val trimmed = newPerson.trim()
                                if (trimmed.isNotEmpty() && trimmed !in people) people.add(trimmed)
                                newPerson = ""
                                addingPerson = false
                            }
                        )
                    } else {
                        Box(
                            Modifier
                                .clip(CircleShape)
                                .background(Turquoise.copy(alpha = 0.10f))
                                .border(1.dp, OutlineColor, CircleShape)
                                .clickable { addingPerson = true }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text("+ Dodaj", color = Turquoise, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Opis", color = TextPrimary, style = MaterialTheme.typography.labelLarge)
                Text(
                    "  (opcjonalnie)",
                    color = TextMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(6.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceVariantColor.copy(alpha = 0.55f))
                    .border(1.dp, OutlineColor.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                BasicTextField(
                    value = description,
                    onValueChange = { description = it },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                    cursorBrush = SolidColor(Turquoise),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(14.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Turquoise.copy(alpha = 0.10f))
                    .border(1.dp, Turquoise.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.TextFields,
                        contentDescription = null,
                        tint = Turquoise,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "  Podpis na zdjęciu",
                        color = Turquoise,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f)
                    )
                    SettingSwitch(checked = captionOn, onCheckedChange = { captionOn = it })
                }
                if (captionOn) {
                    Text(
                        text = caption.ifBlank { "Uzupełnij dane, aby powstał podpis" },
                        color = Cream,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
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
                    text = "  Zapisz w galerii",
                    color = BackgroundBottom,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Thumbnail(photoUri: Uri?) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xFF2E7F87), Color(0xFF16505A))))
            .border(BorderStroke(1.5.dp, TextMuted.copy(alpha = 0.7f)), shape),
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
                    modifier = Modifier.size(20.dp)
                )
                Text("Zdjęcie 1", color = TextPrimary, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = TextMuted,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
    )
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    bold: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(shape)
            .background(SurfaceVariantColor.copy(alpha = 0.55f))
            .border(
                if (highlighted) 1.5.dp else 1.dp,
                if (highlighted) Turquoise else OutlineColor.copy(alpha = 0.7f),
                shape
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (highlighted) Turquoise else TextMuted,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = TextPrimary,
                fontWeight = if (bold) androidx.compose.ui.text.font.FontWeight.Bold
                else androidx.compose.ui.text.font.FontWeight.Medium
            ),
            cursorBrush = SolidColor(Turquoise),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PersonChip(name: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(Turquoise.copy(alpha = 0.18f))
            .border(1.dp, Turquoise.copy(alpha = 0.6f), CircleShape)
            .padding(start = 14.dp, end = 8.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, color = TextPrimary, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.width(6.dp))
        Icon(
            Icons.Filled.Close,
            contentDescription = "Usuń",
            tint = TextMuted,
            modifier = Modifier
                .size(16.dp)
                .clickable(onClick = onRemove)
        )
    }
}

@Composable
private fun AddPersonField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    Box(
        Modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.25f))
            .border(1.dp, Turquoise, CircleShape)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .width(130.dp)
    ) {
        if (value.isEmpty()) {
            Text("Imię i nazwisko", color = TextMuted, style = MaterialTheme.typography.bodySmall)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(color = TextPrimary),
            cursorBrush = SolidColor(Turquoise),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PhotoDetailsScreenPreview() {
    RestoriaTheme { PhotoDetailsScreen() }
}