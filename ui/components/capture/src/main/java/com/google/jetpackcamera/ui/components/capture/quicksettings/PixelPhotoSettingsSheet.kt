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
package com.google.jetpackcamera.ui.components.capture.quicksettings
import com.google.jetpackcamera.model.PhotoResolution

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Timer10
import androidx.compose.material.icons.filled.Timer3
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.CropPortrait
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Pixel Camera-style Photo Settings floating overlay.
 * Displays as a floating card at the top of screen with two-column layout:
 * - Left: Setting labels with current values
 * - Right: Icon button grid for each setting
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PixelPhotoSettingsSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onNavigateToSettings: () -> Unit,
    // Mode check
    isVideoMode: Boolean = false,
    // Flash
    currentFlashMode: FlashSettingMode,
    onFlashModeChanged: (FlashSettingMode) -> Unit,
    // RAW/JPEG
    currentImageProcessingMode: ImageProcessingMode = ImageProcessingMode.JPEG,
    onImageProcessingModeChanged: (ImageProcessingMode) -> Unit = {},
    // Timer
    currentTimerMode: TimerMode,
    onTimerModeChanged: (TimerMode) -> Unit,
    // Aspect Ratio
    currentAspectRatio: AspectRatioMode,
    onAspectRatioChanged: (AspectRatioMode) -> Unit,
    // FPS (video only)
    currentFpsMode: FpsMode = FpsMode.FPS_30,
    onFpsModeChanged: (FpsMode) -> Unit = {},
    // Video Resolution (video only)
    // Video Resolution (video only)
    currentVideoResolution: VideoResolutionMode = VideoResolutionMode.FHD,
    onVideoResolutionChanged: (VideoResolutionMode) -> Unit = {},
    // Photo Resolution (photo only)
    currentPhotoResolution: PhotoResolution = PhotoResolution.STANDARD,
    onPhotoResolutionChanged: (PhotoResolution) -> Unit = {},
    // Grid
    isGridEnabled: Boolean = false,
    onGridToggled: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Semi-transparent scrim
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        // Floating card at top
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it }
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, top = 80.dp, end = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF2A2A2A).copy(alpha = 0.95f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Consume click to prevent dismissal
                    )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // More light (Flash)
                    PixelSettingRow(
                        label = "More light",
                        currentValue = currentFlashMode.displayName,
                        options = FlashSettingMode.entries.toList(),
                        selectedOption = currentFlashMode,
                        onOptionSelected = onFlashModeChanged,
                        iconProvider = { it.icon }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // RAW/JPEG - interactive selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "RAW/JPEG",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = currentImageProcessingMode.displayName,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // RAW buttons row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ImageProcessingMode.entries.forEach { mode ->
                                val isSelected = mode == currentImageProcessingMode
                                val backgroundColor by animateColorAsState(
                                    targetValue = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        Color(0xFF3A3A3A)
                                    },
                                    label = "rawBg"
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(backgroundColor)
                                        .clickable { onImageProcessingModeChanged(mode) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = mode.displayName,
                                        color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.7f),
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timer
                    PixelSettingRow(
                        label = "Timer",
                        currentValue = currentTimerMode.displayName,
                        options = TimerMode.entries.toList(),
                        selectedOption = currentTimerMode,
                        onOptionSelected = onTimerModeChanged,
                        iconProvider = { it.icon }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ratio
                    PixelSettingRow(
                        label = "Ratio",
                        currentValue = currentAspectRatio.displayName,
                        options = AspectRatioMode.entries.toList(),
                        selectedOption = currentAspectRatio,
                        onOptionSelected = onAspectRatioChanged,
                        iconProvider = { it.icon }
                    )

                    // Photo Resolution selector (photo mode only)
                    if (!isVideoMode) {
                        Spacer(modifier = Modifier.height(16.dp))

                        val photoOptions = listOf(PhotoResolution.STANDARD, PhotoResolution.MEDIUM, PhotoResolution.HIGH)
                        val displayNames = mapOf(
                            PhotoResolution.STANDARD to "12MP",
                            PhotoResolution.MEDIUM to "24MP",
                            PhotoResolution.HIGH to "Max"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Resolution",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = displayNames[currentPhotoResolution] ?: "Standard",
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Resolution buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                photoOptions.forEach { resolution ->
                                    val isSelected = resolution == currentPhotoResolution
                                    val backgroundColor by animateColorAsState(
                                        targetValue = if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            Color(0xFF3A3A3A)
                                        },
                                        label = "photoResBg"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(backgroundColor)
                                            .clickable { onPhotoResolutionChanged(resolution) }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = displayNames[resolution] ?: "",
                                            color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.7f),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // FPS selector (video mode only)
                    if (isVideoMode) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Frame rate",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "${currentFpsMode.fps} fps",
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            // FPS buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FpsMode.entries.forEach { fps ->
                                    val isSelected = fps == currentFpsMode
                                    val backgroundColor by animateColorAsState(
                                        targetValue = if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            Color(0xFF3A3A3A)
                                        },
                                        label = "fpsBg"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(backgroundColor)
                                            .clickable { onFpsModeChanged(fps) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = fps.displayName,
                                            color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.7f),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Video Resolution selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Resolution",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = currentVideoResolution.displayName,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            // Resolution buttons (HD, FHD, 4K)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VideoResolutionMode.entries.forEach { resolution ->
                                    val isSelected = resolution == currentVideoResolution
                                    val backgroundColor by animateColorAsState(
                                        targetValue = if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            Color(0xFF3A3A3A)
                                        },
                                        label = "resBg"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(backgroundColor)
                                            .clickable { onVideoResolutionChanged(resolution) }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = resolution.displayName,
                                            color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.7f),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }  // End of isVideoMode block

                    Spacer(modifier = Modifier.height(16.dp))

                    // Grid toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Grid",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = if (isGridEnabled) "On" else "Off",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Grid toggle button
                        val gridBg by animateColorAsState(
                            targetValue = if (isGridEnabled) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color(0xFF3A3A3A)
                            },
                            label = "gridBg"
                        )
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(gridBg)
                                .clickable { onGridToggled(!isGridEnabled) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.GridOn,
                                contentDescription = "Grid",
                                tint = if (isGridEnabled) Color.Black else Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // More settings button - aligned to end
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF3A3A3A))
                                .clickable {
                                    onDismiss()
                                    onNavigateToSettings()
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "More settings",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Single setting row with label on left, icon buttons on right.
 */
@Composable
private fun <T> PixelSettingRow(
    label: String,
    currentValue: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    iconProvider: (T) -> ImageVector,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Label and current value
        Column {
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
            Text(
                text = currentValue,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }

        // Right: Icon buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color(0xFF3A3A3A)
                    },
                    label = "settingIconBg"
                )
                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) {
                        Color.Black
                    } else {
                        Color.White.copy(alpha = 0.7f)
                    },
                    label = "settingIconTint"
                )

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(backgroundColor)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onOptionSelected(option)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconProvider(option),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

// Setting enums

enum class FlashSettingMode(val displayName: String, val icon: ImageVector) {
    OFF("None", Icons.Default.FlashOff),
    NIGHT_SIGHT("Night Sight", Icons.Default.NightsStay),
    AUTO("Auto", Icons.Default.FlashAuto),
    ON("On", Icons.Default.FlashOn)
}

enum class TimerMode(val displayName: String, val icon: ImageVector, val seconds: Int?) {
    OFF("Off", Icons.Default.TimerOff, null),
    THREE_SEC("3s", Icons.Default.Timer3, 3),
    FIVE_SEC("5s", Icons.Default.Timer, 5),
    TEN_SEC("10s", Icons.Default.Timer10, 10)
}

enum class AspectRatioMode(val displayName: String, val icon: ImageVector) {
    RATIO_4_3("4:3", Icons.Outlined.CropFree),
    RATIO_16_9("16:9", Icons.Outlined.CropPortrait)
}

/**
 * Video frame rate mode
 */
enum class FpsMode(val displayName: String, val fps: Int) {
    FPS_24("24", 24),
    FPS_30("30", 30),
    FPS_60("60", 60)
}

/**
 * Video resolution mode for quality selection
 */
enum class VideoResolutionMode(val displayName: String) {
    HD("HD"),
    FHD("FHD"),
    UHD("4K")
}

/**
 * Image processing mode for zero processing / RAW capture
 */
enum class ImageProcessingMode(val displayName: String) {
    JPEG("JPEG"),
    RAW_JPEG("RAW+J"),
    RAW("RAW")
}

// Preview

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PixelSettingRowPreview() {
    Column(
        modifier = Modifier
            .width(360.dp)
            .background(Color(0xFF2A2A2A))
            .padding(20.dp)
    ) {
        PixelSettingRow(
            label = "More light",
            currentValue = "None",
            options = FlashSettingMode.entries.toList(),
            selectedOption = FlashSettingMode.OFF,
            onOptionSelected = {},
            iconProvider = { it.icon }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PixelSettingRow(
            label = "Timer",
            currentValue = "Off",
            options = TimerMode.entries.toList(),
            selectedOption = TimerMode.OFF,
            onOptionSelected = {},
            iconProvider = { it.icon }
        )
    }
}
