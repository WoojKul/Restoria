package com.example.restoria.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val BgTop = Color(0xFF1F3A40)
private val BgBottom = Color(0xFF0D1E23)
private val CardColor = Color(0x14FFFFFF)
private val DashColor = Color(0x40FFFFFF)
private val SheetColor = Color(0xFF13282D)
private val HandleColor = Color(0x40FFFFFF)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFFB4C4C6)
private val TextMuted = Color(0xFF7F9497)
private val Teal = Color(0xFF2EC4B6)
private val Blue = Color(0xFF3A86E8)

@Composable
fun SavedScreen(
    photoUri: Uri?,
    photoName: String = "Zdjęcie przykładowe",
    fileSize: String = "2,4 MB",
    onShareClick: () -> Unit = {},
    onSavePdfClick: () -> Unit = {},
    onBeforeAfterClick: () -> Unit = {},
    onAlbumClick: () -> Unit = {},
    onNextPhotoClick: () -> Unit,
    onDoneClick: () -> Unit
) {
    val photo = rememberPhotoBitmap(photoUri)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgTop, BgBottom)))
    ) {
        if (photo != null) {
            Image(
                bitmap = photo,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(40.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                BgTop.copy(alpha = 0.75f),
                                BgBottom.copy(alpha = 0.95f)
                            )
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x33C9805C), Color.Transparent),
                            center = Offset(0f, 0f),
                            radius = 900f
                        )
                    )
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            SavedCard(
                photo = photo,
                photoName = photoName,
                fileSize = fileSize,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp)
            )

            ShareSheet(
                onShareClick = onShareClick,
                onSavePdfClick = onSavePdfClick,
                onBeforeAfterClick = onBeforeAfterClick,
                onAlbumClick = onAlbumClick,
                onNextPhotoClick = onNextPhotoClick,
                onDoneClick = onDoneClick
            )
        }
    }
}

@Composable
private fun rememberPhotoBitmap(uri: Uri?): ImageBitmap? {
    val context = LocalContext.current
    return produceState<ImageBitmap?>(initialValue = null, uri) {
        value = if (uri == null) {
            null
        } else {
            withContext(Dispatchers.IO) {
                runCatching {
                    val resolver = context.contentResolver

                    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    resolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, bounds)
                    }

                    var sample = 1
                    while (bounds.outWidth / (sample * 2) >= 1080 &&
                        bounds.outHeight / (sample * 2) >= 1080
                    ) {
                        sample *= 2
                    }

                    val options = BitmapFactory.Options().apply { inSampleSize = sample }
                    val decoded = resolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, options)
                    } ?: return@runCatching null

                    val rotation = resolver.openInputStream(uri)?.use {
                        when (ExifInterface(it).getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                        )) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                            else -> 0f
                        }
                    } ?: 0f

                    val result = if (rotation != 0f) {
                        Bitmap.createBitmap(
                            decoded, 0, 0, decoded.width, decoded.height,
                            Matrix().apply { postRotate(rotation) }, true
                        )
                    } else {
                        decoded
                    }

                    result.asImageBitmap()
                }.getOrNull()
            }
        }
    }.value
}

@Composable
private fun SavedCard(
    photo: ImageBitmap?,
    photoName: String,
    fileSize: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(CardColor)
            .drawWithContent {
                drawContent()
                val stroke = 1.5.dp.toPx()
                drawRoundRect(
                    color = DashColor,
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = Size(size.width - stroke, size.height - stroke),
                    cornerRadius = CornerRadius(28.dp.toPx()),
                    style = Stroke(
                        width = stroke,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(10.dp.toPx(), 8.dp.toPx())
                        )
                    )
                )
            }
    ) {
        if (photo != null) {
            Image(
                bitmap = photo,
                contentDescription = "Zapisane zdjęcie",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x660D1E23),
                                Color(0xB30D1E23),
                                Color(0x800D1E23)
                            )
                        )
                    )
            )
        } else {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(text = "Zdjęcie 1", color = TextMuted, fontSize = 15.sp)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .drawBehind {
                        val r = 110.dp.toPx()
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Teal.copy(alpha = 0.35f), Color.Transparent),
                                center = center,
                                radius = r
                            ),
                            radius = r
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Teal, Blue))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Zapisano",
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Zapisano w galerii",
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "$photoName · $fileSize",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ShareSheet(
    onShareClick: () -> Unit,
    onSavePdfClick: () -> Unit,
    onBeforeAfterClick: () -> Unit,
    onAlbumClick: () -> Unit,
    onNextPhotoClick: () -> Unit,
    onDoneClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(SheetColor)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 10.dp, bottom = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(36.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(HandleColor)
        )

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Udostępnij",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            ShareAction(
                icon = Icons.Outlined.Share,
                label = "Wyślij",
                tint = Color(0xFF2EC4B6),
                bg = Color(0xFF1E4747),
                onClick = onShareClick,
                modifier = Modifier.weight(1f)
            )
            ShareAction(
                icon = Icons.Outlined.PictureAsPdf,
                label = "Zapisz PDF",
                tint = Color(0xFFF08A5D),
                bg = Color(0xFF45302C),
                onClick = onSavePdfClick,
                modifier = Modifier.weight(1f)
            )
            ShareAction(
                icon = Icons.Outlined.SwapHoriz,
                label = "Przed i po",
                tint = Color(0xFF5B9BF0),
                bg = Color(0xFF233656),
                onClick = onBeforeAfterClick,
                modifier = Modifier.weight(1f)
            )
            ShareAction(
                icon = Icons.Outlined.Folder,
                label = "Album",
                tint = Color(0xFFB5D98A),
                bg = Color(0xFF2D4232),
                onClick = onAlbumClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(36.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(CircleShape)
                    .background(Color(0x14FFFFFF))
                    .border(1.dp, Color(0x26FFFFFF), CircleShape)
                    .clickable(onClick = onNextPhotoClick),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "Kolejne zdjęcie", color = TextPrimary, fontSize = 15.sp)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(Teal, Blue)))
                    .clickable(onClick = onDoneClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Gotowe",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ShareAction(
    icon: ImageVector,
    label: String,
    tint: Color,
    bg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun SavedScreenPreview() {
    SavedScreen(photoUri = null, onNextPhotoClick = {}, onDoneClick = {})
}