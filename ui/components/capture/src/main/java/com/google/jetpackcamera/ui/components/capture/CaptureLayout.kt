/*
 * Copyright (C) 2025 The Android Open Source Project
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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// these layouts are only concerned with placement. nothing else. no state handling
@Composable
fun PreviewLayout(
    modifier: Modifier = Modifier,
    aspectRatio: com.google.jetpackcamera.model.AspectRatio,
    viewfinder: @Composable (Modifier) -> Unit,
    gridOverlay: @Composable (Modifier) -> Unit = {},
    captureButton: @Composable (Modifier) -> Unit,
    imageWell: @Composable (Modifier) -> Unit,
    flipCameraButton: @Composable (Modifier) -> Unit,
    zoomLevelDisplay: @Composable (Modifier) -> Unit,
    elapsedTimeDisplay: @Composable (Modifier) -> Unit,
    quickSettingsButton: @Composable (Modifier) -> Unit,
    indicatorRow: @Composable (Modifier) -> Unit,
    captureModeToggle: @Composable (Modifier) -> Unit,
    modeSelector: @Composable (Modifier) -> Unit = {},
    quickSettingsOverlay: @Composable (Modifier) -> Unit,
    debugOverlay: @Composable (Modifier) -> Unit,
    debugVisibilityWrapper: (@Composable (@Composable () -> Unit) -> Unit),
    screenFlashOverlay: @Composable (Modifier) -> Unit,
    snackBar: @Composable (Modifier, snackbarHostState: SnackbarHostState) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                // Dark theme styled snackbar - minimal and unobtrusive
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF1C1C1E),  // Dark gray, almost black
                    contentColor = Color.White,
                    actionColor = MaterialTheme.colorScheme.primary,
                    dismissActionContentColor = Color.White.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = modifier.background(Color.Black)) {
            Column {
                indicatorRow(Modifier.statusBarsPadding())

                val ratio = when(aspectRatio) {
                    com.google.jetpackcamera.model.AspectRatio.THREE_FOUR -> 3f/4f
                    com.google.jetpackcamera.model.AspectRatio.NINE_SIXTEEN -> 9f/16f
                    else -> 3f/4f
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio)
                    .clip(RectangleShape)
                    .align(Alignment.CenterHorizontally)
                ) {
                    viewfinder(Modifier)
                    // Grid overlay on top of viewfinder - clipped to viewfinder bounds
                    gridOverlay(Modifier.matchParentSize())
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .safeDrawingPadding()

            ) {
                debugVisibilityWrapper {
                    VerticalMaterialControls(
                        captureButton = captureButton,
                        imageWell = imageWell,
                        flipCameraButton = flipCameraButton,
                        quickSettingsToggleButton = quickSettingsButton,
                        captureModeToggleSwitch = captureModeToggle,
                        bottomSheetQuickSettings = quickSettingsOverlay,
                        zoomControls = zoomLevelDisplay,
                        elapsedTimeDisplay = elapsedTimeDisplay
                    )
                }
                // controls overlay
                snackBar(Modifier, snackbarHostState)
                screenFlashOverlay(Modifier)
            }
            debugOverlay(Modifier)
        }
    }
}

@Composable
private fun VerticalMaterialControls(
    modifier: Modifier = Modifier,
    captureButton: @Composable (Modifier) -> Unit,
    zoomControls: @Composable (Modifier) -> Unit,
    imageWell: @Composable (Modifier) -> Unit,
    flipCameraButton: @Composable (Modifier) -> Unit,
    quickSettingsToggleButton: @Composable (Modifier) -> Unit,
    bottomSheetQuickSettings: @Composable (Modifier) -> Unit,
    captureModeToggleSwitch: @Composable (Modifier) -> Unit,
    elapsedTimeDisplay: @Composable (Modifier) -> Unit
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
            Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                elapsedTimeDisplay(Modifier)

                // zoom controls row
                zoomControls(Modifier)
                // capture button row
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Row that holds flip camera, capture button, and audio
                        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                            // animation fades in/out this component based on quick settings
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                imageWell(Modifier)
                            }
                        }
                        captureButton(Modifier)

                        // right capturebutton item
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            flipCameraButton(Modifier)
                        }
                    }
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)  // Reduced for visual cohesion with capture button
                )

                // bottom controls row - mode toggle centered, full width
                Row(
                    Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // capture mode toggle switch - full width centered
                    captureModeToggleSwitch(Modifier.fillMaxWidth())
                }
            }
        }
        bottomSheetQuickSettings(Modifier)
    }
}

@Preview
@Composable
private fun CaptureLayoutPreview() {
    PreviewLayout(
        modifier = Modifier.background(Color.Black),
        aspectRatio = com.google.jetpackcamera.model.AspectRatio.THREE_FOUR,
        viewfinder = { modifier ->
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .background(Color.DarkGray)
            )
        },
        captureButton = { modifier ->
            Box(
                modifier = modifier
                    .size(80.dp)
                    .background(Color.White)
            )
        },
        flipCameraButton = { modifier ->
            Box(
                modifier = modifier
                    .size(48.dp)
                    .background(Color.Cyan)
            )
        },
        imageWell = { modifier ->
            Box(
                modifier = modifier
                    .size(48.dp)
                    .background(Color.Cyan)
            )
        },
        zoomLevelDisplay = { modifier ->
            Box(
                modifier = modifier
                    .height(48.dp)
                    .fillMaxWidth()
                    .background(Color.Magenta)
            )
        },
        elapsedTimeDisplay = { modifier ->
            Box(
                modifier = modifier
                    .height(24.dp)
                    .fillMaxWidth(0.5f)
                    .background(Color.Red)
            )
        },
        quickSettingsButton = { modifier ->
            Box(
                modifier = modifier
                    .size(48.dp)
                    .background(Color.Yellow)
            )
        },
        indicatorRow = { modifier ->
            Box(
                modifier = modifier
                    .height(48.dp)
                    .fillMaxWidth()
                    .background(Color.Green)
            )
        },
        captureModeToggle = { modifier ->
            Box(
                modifier = modifier
                    .height(48.dp)
                    .fillMaxWidth(0.5f)
                    .background(Color.Blue)
            )
        },
        quickSettingsOverlay = {
            // No-op for preview
        },
        debugOverlay = {
            // No-op for preview
        },
        screenFlashOverlay = {
            // No-op for preview
        },
        snackBar = { _, _ ->
            // No-op for preview
        },
        debugVisibilityWrapper = { content -> content() }
    )
}
