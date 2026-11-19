package com.example.restoria.ui.screens

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.restoria.ui.components.RestoriaBottomBar
import com.example.restoria.ui.components.Tab
import com.example.restoria.ui.components.authBackground
import com.example.restoria.ui.theme.BackgroundBottom
import com.example.restoria.ui.theme.OutlineColor
import com.example.restoria.ui.theme.PrimaryButtonBrush
import com.example.restoria.ui.theme.SurfaceColor
import com.example.restoria.ui.theme.SurfaceVariantColor
import com.example.restoria.ui.theme.TextMuted
import com.example.restoria.ui.theme.TextPrimary
import com.example.restoria.ui.theme.Turquoise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GalleryPhoto(
    val id: Long,
    val uri: Uri,
    val name: String,
    val dateAdded: Long,
    val albumId: Long,
    val albumName: String,
    val path: String
)

private data class Album(val id: Long, val name: String, val cover: Uri, val count: Int)

private val GalleryFilters = listOf("Wszystkie", "Ostatnie", "Skany", "Albumy")
private const val FILTER_ALL = 0
private const val FILTER_RECENT = 1
private const val FILTER_SCANS = 2
private const val FILTER_ALBUMS = 3


@Suppress("DEPRECATION")
private fun loadPhotos(context: Context): List<GalleryPhoto> {
    val result = mutableListOf<GalleryPhoto>()
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media.DATA
    )
    context.contentResolver.query(
        collection,
        projection,
        null,
        null,
        "${MediaStore.Images.Media.DATE_ADDED} DESC"
    )?.use { c ->
        val idCol = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val nameCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        val dateCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
        val bucketIdCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
        val bucketNameCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val dataCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        while (c.moveToNext()) {
            val id = c.getLong(idCol)
            result.add(
                GalleryPhoto(
                    id = id,
                    uri = ContentUris.withAppendedId(collection, id),
                    name = c.getString(nameCol) ?: "",
                    dateAdded = c.getLong(dateCol),
                    albumId = c.getLong(bucketIdCol),
                    albumName = c.getString(bucketNameCol)?.takeIf { it.isNotBlank() } ?: "Inne",
                    path = c.getString(dataCol) ?: ""
                )
            )
        }
    }
    return result
}

private fun GalleryPhoto.isScan(): Boolean {
    val p = path.lowercase()
    val n = name.lowercase()
    return listOf("scan", "skan", "document", "dokument").any { it in p || it in n }
}

private fun selectedText(count: Int): String {
    val word = when {
        count == 1 -> "wybrane"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "wybrane"
        else -> "wybranych"
    }
    return "$count $word"
}

private fun previewPhotos(): List<GalleryPhoto> = List(12) {
    GalleryPhoto(it.toLong(), Uri.EMPTY, "Zdjęcie ${it + 1}", 0L, 1L, "Aparat", "")
}


@Composable
fun GalleryScreen(
    onTabClick: (Tab) -> Unit,
    onCameraClick: () -> Unit,
    onRestoreClick: (List<Uri>) -> Unit = {}
) {
    val context = LocalContext.current
    val inPreview = LocalInspectionMode.current

    val permission = if (Build.VERSION.SDK_INT >= 33) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var hasPermission by remember {
        mutableStateOf(
            inPreview ||
                    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(permission)
    }

    var photos by remember { mutableStateOf(if (inPreview) previewPhotos() else emptyList()) }
    var loading by remember { mutableStateOf(!inPreview) }

    LaunchedEffect(hasPermission) {
        if (inPreview) return@LaunchedEffect
        if (hasPermission) {
            loading = true
            photos = withContext(Dispatchers.IO) { loadPhotos(context) }
        }
        loading = false
    }

    var activeFilter by remember { mutableStateOf(FILTER_ALL) }
    var openAlbumId by remember { mutableStateOf<Long?>(null) }
    var searchOpen by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(setOf<Long>()) }

    BackHandler(enabled = searchOpen || openAlbumId != null) {
        if (searchOpen) {
            searchOpen = false
            query = ""
        } else {
            openAlbumId = null
        }
    }

    val q = query.trim().lowercase()
    val showAlbums = activeFilter == FILTER_ALBUMS && openAlbumId == null

    val albums = remember(photos, q) {
        photos.groupBy { it.albumId }
            .map { (id, list) -> Album(id, list.first().albumName, list.first().uri, list.size) }
            .filter { q.isEmpty() || it.name.lowercase().contains(q) }
    }

    val visiblePhotos = remember(photos, activeFilter, openAlbumId, q) {
        val recentThreshold = System.currentTimeMillis() / 1000 - 30L * 24 * 3600
        val base = when (activeFilter) {
            FILTER_RECENT -> photos.filter { it.dateAdded >= recentThreshold }
            FILTER_SCANS -> photos.filter { it.isScan() }
            FILTER_ALBUMS -> photos.filter { it.albumId == openAlbumId }
            else -> photos
        }
        if (q.isEmpty()) base
        else base.filter { it.name.lowercase().contains(q) || it.albumName.lowercase().contains(q) }
    }

    val title = if (openAlbumId != null) {
        photos.firstOrNull { it.albumId == openAlbumId }?.albumName ?: "Album"
    } else {
        "Galeria"
    }

    Box(Modifier.fillMaxSize().authBackground()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Spacer(Modifier.height(24.dp))

            if (searchOpen) {
                SearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onClose = {
                        searchOpen = false
                        query = ""
                    }
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (openAlbumId != null) {
                        RoundIconButton(onClick = { openAlbumId = null }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Wstecz",
                                tint = TextPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(Modifier.size(12.dp))
                    }
                    Text(
                        text = title,
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    RoundIconButton(onClick = { searchOpen = true }) {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = "Szukaj",
                            tint = TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GalleryFilters.forEachIndexed { index, label ->
                    FilterChip(
                        label = label,
                        active = index == activeFilter,
                        onClick = {
                            activeFilter = index
                            openAlbumId = null
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            val bottomPadding = if (selected.isNotEmpty()) 190.dp else 110.dp

            when {
                !hasPermission -> MessageState(
                    title = "Potrzebuję dostępu do zdjęć",
                    subtitle = "Zezwól Restorii na odczyt zdjęć, żeby pokazać Twoją galerię.",
                    primaryLabel = "Zezwól",
                    onPrimaryClick = { permissionLauncher.launch(permission) },
                    secondaryLabel = "Otwórz ustawienia aplikacji",
                    onSecondaryClick = {
                        context.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        )
                    }
                )

                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Turquoise)
                }

                showAlbums && albums.isEmpty() -> MessageState(title = "Brak albumów")
                !showAlbums && visiblePhotos.isEmpty() -> MessageState(
                    title = when {
                        q.isNotEmpty() -> "Nic nie znaleziono"
                        activeFilter == FILTER_SCANS -> "Brak skanów"
                        activeFilter == FILTER_RECENT -> "Brak ostatnich zdjęć"
                        else -> "Brak zdjęć"
                    }
                )

                showAlbums -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = bottomPadding),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(albums, key = { it.id }) { album ->
                        AlbumCard(album = album, onClick = { openAlbumId = album.id })
                    }
                }

                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = bottomPadding),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(visiblePhotos, key = { it.id }) { photo ->
                        val isSelected = photo.id in selected
                        GalleryTile(
                            photo = photo,
                            selected = isSelected,
                            onClick = {
                                selected = if (isSelected) selected - photo.id else selected + photo.id
                            }
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            if (selected.isNotEmpty()) {
                SelectionBar(
                    count = selected.size,
                    onRestoreClick = {
                        onRestoreClick(photos.filter { it.id in selected }.map { it.uri })
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(8.dp))
            }
            RestoriaBottomBar(
                selected = Tab.GALLERY,
                onTabClick = onTabClick,
                onCameraClick = onCameraClick
            )
        }
    }
}
@Composable
private fun RoundIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(SurfaceVariantColor.copy(alpha = 0.6f))
            .border(1.dp, OutlineColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit, onClose: () -> Unit) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(CircleShape)
                .background(SurfaceVariantColor.copy(alpha = 0.6f))
                .border(1.dp, Turquoise, CircleShape)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Search,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.size(8.dp))
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (query.isEmpty()) {
                    Text(
                        "Szukaj zdjęć i albumów",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                    cursorBrush = SolidColor(Turquoise),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }
        }
        Spacer(Modifier.size(10.dp))
        RoundIconButton(onClick = onClose) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Zamknij wyszukiwanie",
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun FilterChip(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .then(
                if (active) Modifier.background(PrimaryButtonBrush)
                else Modifier
                    .background(SurfaceVariantColor.copy(alpha = 0.6f))
                    .border(1.dp, OutlineColor, CircleShape)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (active) BackgroundBottom else TextPrimary,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun PhotoThumb(uri: Uri, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val request = remember(uri) {
        ImageRequest.Builder(context)
            .data(uri)
            .size(360)
            .crossfade(true)
            .build()
    }
    Box(modifier, contentAlignment = Alignment.Center) {
        Icon(
            Icons.Outlined.Image,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
        AsyncImage(
            model = request,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun GalleryTile(photo: GalleryPhoto, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.88f)
            .clip(shape)
            .background(SurfaceVariantColor.copy(alpha = 0.7f))
            .then(
                if (selected) Modifier.border(2.dp, Turquoise, shape)
                else Modifier.border(1.dp, OutlineColor, shape)
            )
            .clickable(onClick = onClick)
    ) {
        PhotoThumb(photo.uri, Modifier.fillMaxSize())

        if (selected) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Turquoise.copy(alpha = 0.22f))
            )
        }

        SelectionCircle(
            selected = selected,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}

@Composable
private fun SelectionCircle(selected: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(CircleShape)
            .then(
                if (selected) Modifier.background(Turquoise)
                else Modifier
                    .background(Color.Black.copy(alpha = 0.25f))
                    .border(1.5.dp, TextPrimary.copy(alpha = 0.9f), CircleShape)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = BackgroundBottom,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun AlbumCard(album: Album, onClick: () -> Unit) {
    val shape = RoundedCornerShape(22.dp)
    Column(Modifier.clickable(onClick = onClick)) {
        PhotoThumb(
            album.cover,
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape)
                .background(SurfaceVariantColor.copy(alpha = 0.7f))
                .border(1.dp, OutlineColor, shape)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = album.name,
            color = TextPrimary,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Text(
            text = "${album.count}",
            color = TextMuted,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun MessageState(
    title: String,
    subtitle: String? = null,
    primaryLabel: String? = null,
    onPrimaryClick: () -> Unit = {},
    secondaryLabel: String? = null,
    onSecondaryClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.Image,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(36.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = title,
            color = TextPrimary,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        if (subtitle != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = TextMuted,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
        if (primaryLabel != null) {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PrimaryButtonBrush)
                    .clickable(onClick = onPrimaryClick)
                    .padding(horizontal = 32.dp, vertical = 12.dp)
            ) {
                Text(primaryLabel, color = TextPrimary, style = MaterialTheme.typography.labelLarge)
            }
        }
        if (secondaryLabel != null) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = secondaryLabel,
                color = Turquoise,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable(onClick = onSecondaryClick)
            )
        }
    }
}

@Composable
private fun SelectionBar(count: Int, onRestoreClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(28.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(shape)
            .background(SurfaceColor.copy(alpha = 0.95f))
            .border(1.dp, OutlineColor, shape)
            .padding(start = 20.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = selectedText(count),
            color = TextPrimary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .height(44.dp)
                .clip(CircleShape)
                .background(PrimaryButtonBrush)
                .clickable(onClick = onRestoreClick)
                .padding(horizontal = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Odnów",
                color = TextPrimary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun GalleryScreenPreview() {
    com.example.restoria.ui.theme.RestoriaTheme {
        GalleryScreen(
            onTabClick = {},
            onCameraClick = {}
        )
    }
}