/*
 * Copyright (C) 2023 The Android Open Source Project
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
package com.google.jetpackcamera.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Pixel Camera-inspired AMOLED dark color scheme.
 * Uses teal accent on true black surfaces for maximum contrast and OLED power efficiency.
 */
private val PixelCameraDarkScheme = darkColorScheme(
    primary = PixelTeal,
    onPrimary = Color.Black,
    primaryContainer = PixelTealDark,
    onPrimaryContainer = Color.White,
    secondary = PixelTealLight,
    onSecondary = Color.Black,
    secondaryContainer = SurfaceCard,
    onSecondaryContainer = OnSurfaceLight,
    tertiary = PixelTeal,
    onTertiary = Color.Black,
    background = SurfaceBlack,
    onBackground = OnSurfaceLight,
    surface = SurfaceBlack,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = OnSurfaceMedium,
    surfaceContainerLowest = SurfaceBlack,
    surfaceContainerLow = SurfaceDarkGray,
    surfaceContainer = SurfaceElevated,
    surfaceContainerHigh = SurfaceCard,
    surfaceContainerHighest = SurfaceCard,
    outline = OnSurfaceSubtle,
    outlineVariant = OnSurfaceSubtle
)

private val LightColorScheme = lightColorScheme(
    primary = PixelTealDark,
    onPrimary = Color.White,
    primaryContainer = PixelTealLight,
    onPrimaryContainer = Color.Black,
    secondary = PixelTeal,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black
)

@Composable
fun JetpackCameraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                // For dark theme, blend dynamic colors with our AMOLED surfaces
                dynamicDarkColorScheme(context).copy(
                    background = SurfaceBlack,
                    surface = SurfaceBlack,
                    surfaceVariant = SurfaceElevated,
                    surfaceContainerLowest = SurfaceBlack,
                    surfaceContainerLow = SurfaceDarkGray,
                    surfaceContainer = SurfaceElevated
                )
            } else {
                dynamicLightColorScheme(context)
            }
        }
        darkTheme -> PixelCameraDarkScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
