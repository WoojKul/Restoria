package com.example.restoria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.restoria.ui.theme.Blue
import com.example.restoria.ui.theme.OutlineColor
import com.example.restoria.ui.theme.Turquoise
import kotlin.math.roundToInt

@Composable
fun SettingSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = Turquoise,
            checkedBorderColor = Color.Transparent,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = OutlineColor,
            uncheckedBorderColor = Color.Transparent
        )
    )
}

@Composable
fun SettingSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    step: Int = 0
) {
    val thumbSize = 22.dp
    val trackHeight = 6.dp
    val density = LocalDensity.current

    fun snap(raw: Float): Float {
        val v = raw.coerceIn(0f, 100f)
        return if (step > 0) ((v / step).roundToInt() * step).toFloat().coerceIn(0f, 100f) else v
    }

    var widthPx by remember { mutableIntStateOf(0) }
    val thumbPx = with(density) { thumbSize.toPx() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thumbSize + 10.dp)
            .pointerInput(step) {
                detectTapGestures { offset ->
                    val usable = (size.width - thumbPx).coerceAtLeast(1f)
                    onValueChange(snap((offset.x - thumbPx / 2) / usable * 100f))
                }
            }
            .pointerInput(step) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val usable = (size.width - thumbPx).coerceAtLeast(1f)
                    onValueChange(snap((change.position.x - thumbPx / 2) / usable * 100f))
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(RoundedCornerShape(trackHeight))
                .background(OutlineColor.copy(alpha = 0.6f))
                .pointerInput(Unit) {}
        )

        val fraction = value.coerceIn(0f, 100f) / 100f
        Box(
            Modifier
                .fillMaxWidth(fraction)
                .height(trackHeight)
                .clip(RoundedCornerShape(trackHeight))
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(Turquoise, Blue)
                    )
                )
        )

        androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxWidth()) {
            val usableDp = maxWidth - thumbSize
            Box(
                Modifier
                    .offset(x = usableDp * fraction)
                    .size(thumbSize)
                    .shadow(4.dp, CircleShape)
                    .background(Color.White, CircleShape)
            ) {
                Box(
                    Modifier
                        .size(8.dp)
                        .background(Turquoise, CircleShape)
                        .align(Alignment.Center)
                )
            }
        }
    }
}