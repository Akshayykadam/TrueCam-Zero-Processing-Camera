/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.jetpackcamera.ui.components.capture

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Subtle 3×3 grid overlay for composition guidance.
 * Uses low alpha to avoid distracting from the camera preview.
 */
@Composable
fun GridOverlay(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,  // Control grid visibility
    lineColor: Color = Color.White.copy(alpha = 0.4f),
    strokeWidth: Dp = 1.5.dp
) {
    if (!isEnabled) return  // Don't draw if disabled
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val stroke = Stroke(width = strokeWidth.toPx())

        // Vertical lines (divide into 3 columns)
        for (i in 1..2) {
            val x = canvasWidth * i / 3
            drawLine(
                color = lineColor,
                start = Offset(x, 0f),
                end = Offset(x, canvasHeight),
                strokeWidth = stroke.width
            )
        }

        // Horizontal lines (divide into 3 rows)
        for (i in 1..2) {
            val y = canvasHeight * i / 3
            drawLine(
                color = lineColor,
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = stroke.width
            )
        }
    }
}

/**
 * Pixel-style lens selector with floating pills.
 * Shows zoom levels like "UW", "1x", "2x" or custom labels.
 */
@Composable
fun LensSelectorPills(
    lensOptions: List<LensOption>,
    selectedIndex: Int,
    onLensSelected: (Int, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.4f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        lensOptions.forEachIndexed { index, option ->
            val isSelected = index == selectedIndex
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                },
                label = "lensPillBg"
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .clickable(
                        role = Role.Button,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLensSelected(index, option.zoomRatio)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option.label,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        Color.White.copy(alpha = 0.8f)
                    },
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

data class LensOption(
    val label: String,
    val zoomRatio: Float
)

/**
 * Pixel Camera-style mode selector - simple horizontal scrollable text labels
 * Selected mode has subtle pill background, others are plain text.
 */
@Composable
fun ModeSelector(
    modes: List<CameraMode>,
    selectedMode: CameraMode,
    onModeSelected: (CameraMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        modes.forEach { mode ->
            val isSelected = mode == selectedMode
            
            // Animated background for selection
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                } else {
                    Color.Transparent
                },
                animationSpec = tween(durationMillis = 200),
                label = "modeBg"
            )
            
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onModeSelected(mode)
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.displayName,
                    color = if (isSelected) {
                        Color.Black
                    } else {
                        Color.White.copy(alpha = 0.7f)
                    },
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

enum class CameraMode(val displayName: String) {
    ZERO("Zero"),       // Zero Processing - minimal HDR/sharpening
    PHOTO("Photo"),
    VIDEO("Video"),
    NIGHT("Night")      // Night Sight mode
}

/**
 * Pixel-style shutter button with optional timer badge.
 */
@Composable
fun PixelShutterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isVideoMode: Boolean = false,
    isRecording: Boolean = false,
    timerSeconds: Int? = null,
    size: Dp = 72.dp
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "shutterScale"
    )

    val innerColor by animateColorAsState(
        targetValue = when {
            isRecording -> Color.Red
            isVideoMode -> Color.Red
            else -> Color.White
        },
        label = "shutterInnerColor"
    )

    val innerSize by animateFloatAsState(
        targetValue = when {
            isRecording -> 0.35f  // Small red square during recording
            isVideoMode -> 0.5f
            else -> 0.85f
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "shutterInnerSize"
    )

    val innerShape = if (isRecording) {
        RoundedCornerShape(4.dp)
    } else {
        CircleShape
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        tryAwaitRelease()
                    },
                    onTap = { onClick() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer ring
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 4.dp,
                    color = Color.White,
                    shape = CircleShape
                )
        )

        // Inner circle/square
        Box(
            modifier = Modifier
                .size(size * innerSize)
                .clip(innerShape)
                .background(innerColor),
            contentAlignment = Alignment.Center
        ) {
            // Timer badge
            timerSeconds?.let { seconds ->
                Text(
                    text = seconds.toString(),
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Camera switch button with flip animation.
 */
@Composable
fun PixelCameraSwitchButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 48.dp
) {
    val haptic = LocalHapticFeedback.current

    IconButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier.size(size),
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.Outlined.Cameraswitch,
            contentDescription = "Switch camera",
            tint = Color.White,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}

// Previews

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun GridOverlayPreview() {
    Box(modifier = Modifier.size(300.dp)) {
        GridOverlay()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun LensSelectorPillsPreview() {
    LensSelectorPills(
        lensOptions = listOf(
            LensOption("UW", 0.6f),
            LensOption("1x", 1f),
            LensOption("2x", 2f)
        ),
        selectedIndex = 1,
        onLensSelected = { _, _ -> }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ModeSelectorPreview() {
    ModeSelector(
        modes = CameraMode.entries,
        selectedMode = CameraMode.PHOTO,
        onModeSelected = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PixelShutterButtonPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        PixelShutterButton(onClick = {})
        PixelShutterButton(onClick = {}, isVideoMode = true)
        PixelShutterButton(onClick = {}, isRecording = true)
        PixelShutterButton(onClick = {}, timerSeconds = 5)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PixelCameraSwitchButtonPreview() {
    PixelCameraSwitchButton(onClick = {})
}
