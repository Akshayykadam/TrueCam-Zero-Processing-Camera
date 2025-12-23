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
package com.google.jetpackcamera.feature.preview

import android.Manifest
import android.content.ContentResolver
import android.os.Build
import android.util.Log
import android.util.Range
import androidx.camera.core.SurfaceRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.google.jetpackcamera.ui.uistate.capture.AspectRatioUiState
import com.google.jetpackcamera.model.PhotoResolution
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.tracing.Trace
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.jetpackcamera.core.camera.InitialRecordingSettings
import com.google.jetpackcamera.core.camera.VideoRecordingState
import com.google.jetpackcamera.model.AspectRatio
import com.google.jetpackcamera.model.CaptureEvent
import com.google.jetpackcamera.model.CaptureMode
import com.google.jetpackcamera.model.ConcurrentCameraMode
import com.google.jetpackcamera.model.DynamicRange
import com.google.jetpackcamera.model.ExternalCaptureMode
import com.google.jetpackcamera.model.FlashMode
import com.google.jetpackcamera.model.ImageCaptureEvent
import com.google.jetpackcamera.model.ImageOutputFormat
import com.google.jetpackcamera.model.LensFacing
import com.google.jetpackcamera.model.LensToZoom
import com.google.jetpackcamera.model.StreamConfig
import com.google.jetpackcamera.model.TestPattern
import com.google.jetpackcamera.model.VideoCaptureEvent
import com.google.jetpackcamera.ui.components.capture.AmplitudeToggleButton
import com.google.jetpackcamera.ui.components.capture.CAPTURE_MODE_TOGGLE_BUTTON
import com.google.jetpackcamera.ui.components.capture.CaptureButton
import com.google.jetpackcamera.ui.components.capture.CaptureModeToggleButton
import com.google.jetpackcamera.ui.components.capture.ELAPSED_TIME_TAG
import com.google.jetpackcamera.ui.components.capture.ElapsedTimeText
import com.google.jetpackcamera.ui.components.capture.FLIP_CAMERA_BUTTON
import com.google.jetpackcamera.ui.components.capture.FlipCameraButton
import com.google.jetpackcamera.ui.components.capture.ImageWell
import com.google.jetpackcamera.ui.components.capture.PauseResumeToggleButton
import com.google.jetpackcamera.ui.components.capture.PreviewDisplay
import com.google.jetpackcamera.ui.components.capture.PreviewLayout
import com.google.jetpackcamera.ui.components.capture.R
import com.google.jetpackcamera.ui.components.capture.ScreenFlashScreen
import com.google.jetpackcamera.ui.components.capture.StabilizationIcon
import com.google.jetpackcamera.ui.components.capture.TestableSnackbar
import com.google.jetpackcamera.ui.components.capture.VIDEO_QUALITY_TAG
import com.google.jetpackcamera.ui.components.capture.VideoQualityIcon
import com.google.jetpackcamera.ui.components.capture.ZoomButtonRow
import com.google.jetpackcamera.ui.components.capture.ZoomState
import com.google.jetpackcamera.ui.components.capture.debouncedOrientationFlow
import com.google.jetpackcamera.ui.components.capture.GridOverlay
import com.google.jetpackcamera.ui.components.capture.LensSelectorPills
import com.google.jetpackcamera.ui.components.capture.LensOption
import com.google.jetpackcamera.ui.components.capture.ModeSelector
import com.google.jetpackcamera.ui.components.capture.CameraMode
import com.google.jetpackcamera.ui.components.capture.debug.DebugOverlay
import com.google.jetpackcamera.ui.components.capture.quicksettings.QuickSettingsBottomSheet
import com.google.jetpackcamera.ui.components.capture.quicksettings.PixelPhotoSettingsSheet
import com.google.jetpackcamera.ui.components.capture.quicksettings.FlashSettingMode
import com.google.jetpackcamera.ui.components.capture.quicksettings.TimerMode
import com.google.jetpackcamera.ui.components.capture.quicksettings.FpsMode
import com.google.jetpackcamera.ui.components.capture.quicksettings.VideoResolutionMode

import com.google.jetpackcamera.ui.components.capture.quicksettings.AspectRatioMode
import com.google.jetpackcamera.model.VideoQuality
import com.google.jetpackcamera.ui.components.capture.quicksettings.ui.FlashModeIndicator
import com.google.jetpackcamera.ui.components.capture.quicksettings.ui.HdrIndicator
import com.google.jetpackcamera.ui.components.capture.quicksettings.ui.ToggleQuickSettingsButton
import com.google.jetpackcamera.ui.uistate.DisableRationale
import com.google.jetpackcamera.ui.uistate.capture.AudioUiState
import com.google.jetpackcamera.ui.uistate.capture.CaptureButtonUiState
import com.google.jetpackcamera.ui.uistate.capture.CaptureModeToggleUiState
import com.google.jetpackcamera.ui.uistate.capture.DebugUiState
import com.google.jetpackcamera.ui.uistate.capture.FlashModeUiState
import com.google.jetpackcamera.ui.uistate.capture.FlipLensUiState
import com.google.jetpackcamera.ui.uistate.capture.ImageWellUiState
import com.google.jetpackcamera.ui.uistate.capture.ScreenFlashUiState
import com.google.jetpackcamera.ui.uistate.capture.ZoomControlUiState
import com.google.jetpackcamera.ui.uistate.capture.ZoomUiState
import com.google.jetpackcamera.ui.uistate.capture.compound.CaptureUiState
import com.google.jetpackcamera.ui.uistate.capture.compound.FocusedQuickSetting
import com.google.jetpackcamera.ui.uistate.capture.compound.QuickSettingsUiState
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch

private const val TAG = "PreviewScreen"

/**
 * Screen used for the Preview feature.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PreviewScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToPostCapture: () -> Unit,
    onCaptureEvent: (CaptureEvent) -> Unit,
    modifier: Modifier = Modifier,
    onRequestWindowColorMode: (Int) -> Unit = {},
    onFirstFrameCaptureCompleted: () -> Unit = {},
    viewModel: PreviewViewModel = hiltViewModel()
) {
    Log.d(TAG, "PreviewScreen")

    val captureUiState: CaptureUiState by viewModel.captureUiState.collectAsState()

    val screenFlashUiState: ScreenFlashUiState
        by viewModel.screenFlash.screenFlashUiState.collectAsState()

    val surfaceRequest: SurfaceRequest?
        by viewModel.surfaceRequest.collectAsState()

    LifecycleStartEffect(Unit) {
        viewModel.startCamera()
        onStopOrDispose {
            viewModel.stopCamera()
        }
    }

    val currentOnCaptureEvent by rememberUpdatedState(onCaptureEvent)
    LaunchedEffect(Unit) {
        for (event in viewModel.captureEvents) {
            currentOnCaptureEvent(event)
            if (event is ImageCaptureEvent.SingleImageCached ||
                event is VideoCaptureEvent.VideoCached
            ) {
                onNavigateToPostCapture()
            }
        }
    }

    if (Trace.isEnabled()) {
        LaunchedEffect(onFirstFrameCaptureCompleted) {
            snapshotFlow { captureUiState }
                .transformWhile {
                    var continueCollecting = true
                    (it as? CaptureUiState.Ready)?.let { ready ->
                        if (ready.sessionFirstFrameTimestamp > 0) {
                            emit(Unit)
                            continueCollecting = false
                        }
                    }
                    continueCollecting
                }.collect {
                    onFirstFrameCaptureCompleted()
                }
        }
    }

    when (val currentUiState = captureUiState) {
        is CaptureUiState.NotReady -> LoadingScreen()
        is CaptureUiState.Ready -> {
            var initialRecordingSettings by remember {
                mutableStateOf<InitialRecordingSettings?>(
                    null
                )
            }

            val context = LocalContext.current
            LaunchedEffect(Unit) {
                debouncedOrientationFlow(context).collect(viewModel::setDisplayRotation)
            }
            val scope = rememberCoroutineScope()
            val zoomState = remember {
                // the initialZoomLevel must be fetched from the settings, not the cameraState.
                // since we want to reset the ZoomState on flip, the zoomstate of the cameraState
                // may not yet be congruent with the settings

                ZoomState(
                    initialZoomLevel = (
                        currentUiState.zoomControlUiState as?
                            ZoomControlUiState.Enabled
                        )
                        ?.initialZoomRatio
                        ?: 1f,
                    onAnimateStateChanged = viewModel::setZoomAnimationState,
                    onChangeZoomLevel = viewModel::changeZoomRatio,
                    zoomRange = (currentUiState.zoomUiState as? ZoomUiState.Enabled)
                        ?.primaryZoomRange
                        ?: Range(1f, 1f)
                )
            }

            LaunchedEffect(
                (currentUiState.flipLensUiState as? FlipLensUiState.Available)
                    ?.selectedLensFacing
            ) {
                zoomState.onChangeLens(
                    newInitialZoomLevel = (
                        currentUiState.zoomControlUiState as?
                            ZoomControlUiState.Enabled
                        )
                        ?.initialZoomRatio
                        ?: 1f,
                    newZoomRange = (currentUiState.zoomUiState as? ZoomUiState.Enabled)
                        ?.primaryZoomRange
                        ?: Range(1f, 1f)
                )
            }
            // todo(kc) handle reset certain values after video recording is complete
            LaunchedEffect(currentUiState.videoRecordingState) {
                with(currentUiState.videoRecordingState) {
                    when (this) {
                        is VideoRecordingState.Starting -> {
                            initialRecordingSettings = this.initialRecordingSettings
                        }

                        is VideoRecordingState.Inactive -> {
                            initialRecordingSettings?.let {
                                val oldPrimaryLensFacing = it.lensFacing
                                val oldZoomRatios = it.zoomRatios
                                val oldAudioEnabled = it.isAudioEnabled
                                Log.d(TAG, "reset pre recording settings")
                                viewModel.setAudioEnabled(oldAudioEnabled)
                                viewModel.setLensFacing(oldPrimaryLensFacing)
                                zoomState.apply {
                                    absoluteZoom(
                                        targetZoomLevel = oldZoomRatios[oldPrimaryLensFacing] ?: 1f,
                                        lensToZoom = LensToZoom.PRIMARY
                                    )
                                    absoluteZoom(
                                        targetZoomLevel = oldZoomRatios[oldPrimaryLensFacing.flip()]
                                            ?: 1f,
                                        lensToZoom = LensToZoom.SECONDARY
                                    )
                                }
                            }
                            initialRecordingSettings = null
                        }

                        is VideoRecordingState.Active -> {}
                    }
                }
            }

            ContentScreen(
                modifier = modifier,
                captureUiState = currentUiState,
                screenFlashUiState = screenFlashUiState,
                surfaceRequest = surfaceRequest,
                onNavigateToSettings = onNavigateToSettings,
                onClearUiScreenBrightness = viewModel::setClearUiScreenBrightness,
                onSetLensFacing = viewModel::setLensFacing,
                onTapToFocus = viewModel::tapToFocus,
                onSetTestPattern = viewModel::setTestPattern,
                onSetImageWell = viewModel::imageWellToRepository,
                onAbsoluteZoom = { zoomRatio: Float, lensToZoom: LensToZoom ->
                    scope.launch {
                        zoomState.absoluteZoom(
                            zoomRatio,
                            lensToZoom
                        )
                    }
                },
                onScaleZoom = { zoomRatio: Float, lensToZoom: LensToZoom ->
                    scope.launch {
                        zoomState.scaleZoom(
                            zoomRatio,
                            lensToZoom
                        )
                    }
                },
                onAnimateZoom = { zoomRatio: Float, lensToZoom: LensToZoom ->
                    scope.launch {
                        zoomState.animatedZoom(
                            targetZoomLevel = zoomRatio,
                            lensToZoom = lensToZoom
                        )
                    }
                },
                onIncrementZoom = { zoomRatio: Float, lensToZoom: LensToZoom ->
                    scope.launch {
                        zoomState.incrementZoom(
                            zoomRatio,
                            lensToZoom
                        )
                    }
                },

                onSetCaptureMode = viewModel::setCaptureMode,
                onChangeFlash = viewModel::setFlash,
                onChangeAspectRatio = viewModel::setAspectRatio,
                onChangeTargetFrameRate = viewModel::setTargetFrameRate,
                onChangeVideoQuality = viewModel::setVideoQuality,
                onChangePhotoResolution = viewModel::setPhotoResolution,
                onSetStreamConfig = viewModel::setStreamConfig,
                onChangeDynamicRange = viewModel::setDynamicRange,
                onChangeConcurrentCameraMode = viewModel::setConcurrentCameraMode,
                onChangeImageFormat = viewModel::setImageFormat,
                onDisabledCaptureMode = viewModel::enqueueDisabledHdrToggleSnackBar,
                onToggleQuickSettings = viewModel::toggleQuickSettings,
                onSetFocusedSetting = viewModel::setFocusedSetting,
                onToggleDebugOverlay = viewModel::toggleDebugOverlay,
                onToggleDebugHidingComponents = viewModel::toggleDebugHidingComponents,
                onSetPause = viewModel::setPaused,
                onSetAudioEnabled = viewModel::setAudioEnabled,
                onCaptureImage = viewModel::captureImage,
                onStartVideoRecording = viewModel::startVideoRecording,
                onStopVideoRecording = viewModel::stopVideoRecording,
                onLockVideoRecording = viewModel::setLockedRecording,
                onRequestWindowColorMode = onRequestWindowColorMode,
                onSnackBarResult = viewModel::onSnackBarResult,
                onNavigatePostCapture = onNavigateToPostCapture
            )
            val readStoragePermission: PermissionState = rememberPermissionState(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            LaunchedEffect(readStoragePermission.status) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P ||
                    readStoragePermission.status.isGranted
                ) {
                    viewModel.updateLastCapturedMedia()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentScreen(
    captureUiState: CaptureUiState.Ready,
    screenFlashUiState: ScreenFlashUiState,
    surfaceRequest: SurfaceRequest?,
    modifier: Modifier = Modifier,
    onNavigateToSettings: () -> Unit = {},
    onClearUiScreenBrightness: (Float) -> Unit = {},
    onSetCaptureMode: (CaptureMode) -> Unit = {},
    onSetLensFacing: (newLensFacing: LensFacing) -> Unit = {},
    onTapToFocus: (x: Float, y: Float) -> Unit = { _, _ -> },
    onSetTestPattern: (TestPattern) -> Unit = {},
    onSetImageWell: () -> Unit = {},
    onAbsoluteZoom: (Float, LensToZoom) -> Unit = { _, _ -> },
    onScaleZoom: (Float, LensToZoom) -> Unit = { _, _ -> },
    onIncrementZoom: (Float, LensToZoom) -> Unit = { _, _ -> },
    onAnimateZoom: (Float, LensToZoom) -> Unit = { _, _ -> },
    onChangeFlash: (FlashMode) -> Unit = {},
    onChangeAspectRatio: (AspectRatio) -> Unit = {},
    onChangeTargetFrameRate: (Int) -> Unit = {},
    onChangeVideoQuality: (VideoQuality) -> Unit = {},
    onChangePhotoResolution: (PhotoResolution) -> Unit = {},
    onSetStreamConfig: (StreamConfig) -> Unit = {},
    onChangeDynamicRange: (DynamicRange) -> Unit = {},
    onChangeConcurrentCameraMode: (ConcurrentCameraMode) -> Unit = {},
    onChangeImageFormat: (ImageOutputFormat) -> Unit = {},
    onDisabledCaptureMode: (DisableRationale) -> Unit = {},
    onToggleQuickSettings: () -> Unit = {},
    onSetFocusedSetting: (FocusedQuickSetting) -> Unit = {},
    onToggleDebugOverlay: () -> Unit = {},
    onToggleDebugHidingComponents: () -> Unit = {},
    onSetPause: (Boolean) -> Unit = {},
    onSetAudioEnabled: (Boolean) -> Unit = {},
    onCaptureImage: (ContentResolver, TimerMode) -> Unit = { _, _ -> },
    onStartVideoRecording: () -> Unit = {},
    onStopVideoRecording: () -> Unit = {},
    onLockVideoRecording: (Boolean) -> Unit = {},
    onRequestWindowColorMode: (Int) -> Unit = {},
    onSnackBarResult: (String) -> Unit = {},
    onNavigatePostCapture: () -> Unit = {}
) {
    val onFlipCamera = {
        if (captureUiState.flipLensUiState is FlipLensUiState.Available) {
            onSetLensFacing(
                (
                    captureUiState.flipLensUiState as FlipLensUiState.Available
                    )
                    .selectedLensFacing.flip()
            )
        }
    }

    val isAudioEnabled = remember(captureUiState) {
        captureUiState.audioUiState is AudioUiState.Enabled.On
    }
    val onToggleAudio = remember(isAudioEnabled) {
        {
            onSetAudioEnabled(!isAudioEnabled)
        }
    }

    // Shared UI state for settings and grid
    var isGridEnabled by remember { mutableStateOf(false) }
    var selectedUiMode by remember { mutableStateOf(CameraMode.PHOTO) }

    var currentTimerMode by remember { mutableStateOf(TimerMode.OFF) }



    val currentAspectRatio = if (captureUiState.quickSettingsUiState is QuickSettingsUiState.Available) {
         val aspectState = (captureUiState.quickSettingsUiState as QuickSettingsUiState.Available).aspectRatioUiState
         if (aspectState is AspectRatioUiState.Available) aspectState.selectedAspectRatio else AspectRatio.THREE_FOUR
    } else {
        AspectRatio.THREE_FOUR
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LayoutWrapper(
        modifier = modifier,
        currentAspectRatio = currentAspectRatio,
        isGridEnabled = isGridEnabled,  // Pass grid state
        hdrIndicator = { HdrIndicator(modifier = it, hdrUiState = captureUiState.hdrUiState) },
        flashModeIndicator = {
            FlashModeIndicator(
                modifier = it,
                flashModeUiState = captureUiState.flashModeUiState
            )
        },
        onFlashClick = {
            // Cycle through flash modes: Off -> Auto -> On -> Off
            val currentFlash = when (captureUiState.flashModeUiState) {
                is FlashModeUiState.Available -> 
                    (captureUiState.flashModeUiState as FlashModeUiState.Available).selectedFlashMode
                else -> FlashMode.OFF
            }
            val nextFlash = when (currentFlash) {
                FlashMode.OFF -> FlashMode.AUTO
                FlashMode.AUTO -> FlashMode.ON
                FlashMode.ON -> FlashMode.OFF
                FlashMode.LOW_LIGHT_BOOST -> FlashMode.OFF
            }
            onChangeFlash(nextFlash)
        },
        videoQualityIndicator = {
            VideoQualityIcon(
                captureUiState.videoQuality,
                Modifier.testTag(VIDEO_QUALITY_TAG)
            )
        },
        stabilizationIndicator = {
            StabilizationIcon(
                modifier = it,
                stabilizationUiState = captureUiState.stabilizationUiState
            )
        },

        viewfinder = {
            PreviewDisplay(
                previewDisplayUiState = captureUiState.previewDisplayUiState,
                onFlipCamera = onFlipCamera,
                onTapToFocus = onTapToFocus,
                onScaleZoom = { onScaleZoom(it, LensToZoom.PRIMARY) },
                surfaceRequest = surfaceRequest,
                onRequestWindowColorMode = onRequestWindowColorMode,
                focusMeteringUiState = captureUiState.focusMeteringUiState
            )
        },
        captureButton = {
            fun runCaptureAction(action: () -> Unit) {
                if ((captureUiState.quickSettingsUiState as? QuickSettingsUiState.Available)
                        ?.quickSettingsIsOpen == true
                ) {
                    onToggleQuickSettings()
                }
                action()
            }
            CaptureButton(
                captureButtonUiState = captureUiState.captureButtonUiState,
                isQuickSettingsOpen = (
                    captureUiState.quickSettingsUiState as?
                        QuickSettingsUiState.Available
                    )?.quickSettingsIsOpen ?: false,
                onCaptureImage = {
                    runCaptureAction {
                        onCaptureImage(it, currentTimerMode)
                    }
                },
                onIncrementZoom = { targetZoom ->
                    onIncrementZoom(targetZoom, LensToZoom.PRIMARY)
                },
                onToggleQuickSettings = onToggleQuickSettings,
                onStartVideoRecording = {},
                onStopVideoRecording = {},
                onLockVideoRecording = {}
            )
        },
        flipCameraButton = {
            FlipCameraButton(
                modifier = Modifier.testTag(FLIP_CAMERA_BUTTON),
                onClick = onFlipCamera,
                flipLensUiState = captureUiState.flipLensUiState,
                // enable only when phone has front and rear camera
                enabledCondition = when (val flipLensUiState = captureUiState.flipLensUiState) {
                    is FlipLensUiState.Available -> flipLensUiState.availableLensFacings.size > 1
                    FlipLensUiState.Unavailable -> false
                }
            )
        },
        zoomLevelDisplay = {
            // Get zoom levels and current ratio from state
            val zoomState = captureUiState.zoomControlUiState
            if (zoomState is ZoomControlUiState.Enabled && zoomState.zoomLevels.isNotEmpty()) {
                val currentZoom = zoomState.primaryZoomRatio ?: 1f
                // Find the closest matching index for the current zoom
                val selectedIndex = zoomState.zoomLevels.indexOfFirst { 
                    kotlin.math.abs(it - currentZoom) < 0.1f 
                }.takeIf { it >= 0 } ?: 0
                
                LensSelectorPills(
                    modifier = Modifier.padding(bottom = 16.dp),  // More padding from viewfinder edge
                    lensOptions = zoomState.zoomLevels.map { zoom ->
                        LensOption(
                            label = when {
                                zoom < 1f -> ".5"
                                zoom == 1f -> "1x"
                                else -> "${zoom.toInt()}x"
                            },
                            zoomRatio = zoom
                        )
                    },
                    selectedIndex = selectedIndex,
                    onLensSelected = { _, zoomRatio ->
                        onAnimateZoom(zoomRatio, LensToZoom.PRIMARY)
                    }
                )
            }
        },
                elapsedTimeDisplay = {},
        quickSettingsButton = {
            AnimatedVisibility(
                visible = (captureUiState.videoRecordingState !is VideoRecordingState.Active),
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(delayMillis = 1_500))
            ) {
                ToggleQuickSettingsButton(
                    modifier = it,
                    toggleBottomSheet = onToggleQuickSettings,
                    isOpen = (
                        captureUiState.quickSettingsUiState
                            as? QuickSettingsUiState.Available
                        )?.quickSettingsIsOpen == true
                )
            }
        },
        audioToggleButton = {
            AmplitudeToggleButton(
                modifier = it,
                onToggleAudio = onToggleAudio,
                audioUiState = captureUiState.audioUiState
            )
        },
        captureModeToggle = {
             // Photo Only Mode - No Selector needed
        },
        quickSettingsOverlay = {
            // Map FlashMode to FlashSettingMode
            val currentFlashMode = when (captureUiState.quickSettingsUiState) {
                is QuickSettingsUiState.Available -> {
                    val flashState = (captureUiState.quickSettingsUiState as QuickSettingsUiState.Available).flashModeUiState
                    when (flashState) {
                        is com.google.jetpackcamera.ui.uistate.capture.FlashModeUiState.Available -> {
                            when (flashState.selectedFlashMode) {
                                FlashMode.OFF -> FlashSettingMode.OFF
                                FlashMode.ON -> FlashSettingMode.ON
                                FlashMode.AUTO -> FlashSettingMode.AUTO
                                FlashMode.LOW_LIGHT_BOOST -> FlashSettingMode.NIGHT_SIGHT
                            }
                        }
                        else -> FlashSettingMode.OFF
                    }
                }
                else -> FlashSettingMode.OFF
            }
            
            // Map AspectRatio to AspectRatioMode  
            val currentAspectRatio = when (captureUiState.quickSettingsUiState) {
                is QuickSettingsUiState.Available -> {
                    val aspectState = (captureUiState.quickSettingsUiState as QuickSettingsUiState.Available).aspectRatioUiState
                    when (aspectState) {
                        is com.google.jetpackcamera.ui.uistate.capture.AspectRatioUiState.Available -> {
                            when (aspectState.selectedAspectRatio) {
                                AspectRatio.THREE_FOUR -> AspectRatioMode.RATIO_4_3
                                AspectRatio.NINE_SIXTEEN -> AspectRatioMode.RATIO_16_9
                                else -> AspectRatioMode.RATIO_4_3
                            }
                        }
                        else -> AspectRatioMode.RATIO_4_3
                    }
                }
                else -> AspectRatioMode.RATIO_4_3
            }



            val isQuickSettingsOpen = (captureUiState.quickSettingsUiState as? QuickSettingsUiState.Available)?.quickSettingsIsOpen == true
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            
            // Uses shared state from ContentScreen level: currentTimerMode, isGridEnabled, currentFpsMode, currentImageProcessingMode
            // Check if video mode is active
            val isVideoMode = captureUiState.captureButtonUiState.let { buttonState ->
                when (buttonState) {
                    is CaptureButtonUiState.Enabled.Idle -> buttonState.captureMode == CaptureMode.VIDEO_ONLY
                    is CaptureButtonUiState.Enabled.Recording -> true
                    else -> false
                }
            }
            
            if (isQuickSettingsOpen) {
                PixelPhotoSettingsSheet(
                    sheetState = sheetState,
                    onDismiss = onToggleQuickSettings,
                    onNavigateToSettings = onNavigateToSettings,
                    currentFlashMode = currentFlashMode,
                    onFlashModeChanged = { flashSettingMode ->
                        val mappedFlashMode = when (flashSettingMode) {
                            FlashSettingMode.OFF -> FlashMode.OFF
                            FlashSettingMode.ON -> FlashMode.ON
                            FlashSettingMode.AUTO -> FlashMode.AUTO
                            FlashSettingMode.NIGHT_SIGHT -> FlashMode.LOW_LIGHT_BOOST
                        }
                        onChangeFlash(mappedFlashMode)
                    },

                    currentTimerMode = currentTimerMode,
                    onTimerModeChanged = { currentTimerMode = it },
                    currentAspectRatio = currentAspectRatio,
                    onAspectRatioChanged = { aspectMode ->
                        val mappedAspectRatio = when (aspectMode) {
                            AspectRatioMode.RATIO_4_3 -> AspectRatio.THREE_FOUR
                            AspectRatioMode.RATIO_16_9 -> AspectRatio.NINE_SIXTEEN
                        }
                        onChangeAspectRatio(mappedAspectRatio)
                    },
                    isGridEnabled = isGridEnabled,
                    onGridToggled = { isGridEnabled = it }
                )
            }
        },
        debugOverlay = { modifier, extraControls ->
            (captureUiState.debugUiState as? DebugUiState.Enabled)?.let {
                DebugOverlay(
                    modifier = modifier,
                    toggleIsOpen = onToggleDebugOverlay,
                    debugUiState = it,
                    onSetTestPattern = onSetTestPattern,
                    onChangeZoomRatio = { f: Float -> onAbsoluteZoom(f, LensToZoom.PRIMARY) },
                    extraControls = extraControls.orEmpty(),
                    onToggleHidingComponents = onToggleDebugHidingComponents
                )
            }
        },
        debugVisibilityWrapper = { content ->
            val uiState = captureUiState.debugUiState
            if (uiState !is DebugUiState.Enabled || !uiState.debugHidingComponents) {
                content()
            }
        },
        screenFlashOverlay = {
            // Screen flash overlay that stays on top of everything but invisible normally. This should
            // not be enabled based on whether screen flash is enabled because a previous image capture
            // may still be running after flash mode change and clear actions (e.g. brightness restore)
            // may need to be handled later. Compose smart recomposition should be able to optimize this
            // if the relevant states are no longer changing.
            ScreenFlashScreen(
                screenFlashUiState = screenFlashUiState,
                onInitialBrightnessCalculated = onClearUiScreenBrightness
            )
        },
        snackBar = { modifier, snackbarHostState ->
            val snackBarData = captureUiState.snackBarUiState.snackBarQueue.peek()
            if (snackBarData != null) {
                TestableSnackbar(
                    modifier = modifier.testTag(snackBarData.testTag),
                    snackbarToShow = snackBarData,
                    snackbarHostState = snackbarHostState,
                    onSnackbarResult = onSnackBarResult
                )
            }
        },
        pauseToggleButton = {
            PauseResumeToggleButton(
                onSetPause = onSetPause,
                currentRecordingState = captureUiState.videoRecordingState
            )
        },
        imageWell = { modifier ->
            if (captureUiState.externalCaptureMode == ExternalCaptureMode.Standard) {
                (captureUiState.imageWellUiState as? ImageWellUiState.LastCapture)?.let { state ->
                    val context = LocalContext.current
                    ImageWell(
                        modifier = modifier,
                        imageWellUiState = state,
                        onClick = {
                            onSetImageWell()
                            // Open in system gallery instead of in-app viewer
                            try {
                                val uri = state.mediaDescriptor.uri
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "image/*")
                                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback to in-app if no gallery app found
                                onNavigatePostCapture()
                            }
                        }
                    )
                }
            }
        }
    )
        
        // Countdown Overlay
        if (captureUiState.countdownSeconds != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = captureUiState.countdownSeconds.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    fontSize = 120.sp
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.size(50.dp))
        Text(text = stringResource(R.string.camera_not_ready), color = Color.White)
    }
}

@Composable
private fun LayoutWrapper(
    modifier: Modifier = Modifier,
    isGridEnabled: Boolean = false,  // Grid visibility control
    currentAspectRatio: AspectRatio,
    viewfinder: @Composable (modifier: Modifier) -> Unit,
    captureButton: @Composable (modifier: Modifier) -> Unit,
    flipCameraButton: @Composable (modifier: Modifier) -> Unit,
    zoomLevelDisplay: @Composable (modifier: Modifier) -> Unit,
    elapsedTimeDisplay: @Composable (modifier: Modifier) -> Unit,
    quickSettingsButton: @Composable (modifier: Modifier) -> Unit,
    flashModeIndicator: @Composable (modifier: Modifier) -> Unit,
    onFlashClick: () -> Unit = {},  // Flash toggle callback
    hdrIndicator: @Composable (modifier: Modifier) -> Unit,
    videoQualityIndicator: @Composable (modifier: Modifier) -> Unit,
    stabilizationIndicator: @Composable (modifier: Modifier) -> Unit,
    pauseToggleButton: @Composable (modifier: Modifier) -> Unit,
    audioToggleButton: @Composable (modifier: Modifier) -> Unit,
    captureModeToggle: @Composable (modifier: Modifier) -> Unit,
    imageWell: @Composable (modifier: Modifier) -> Unit,
    quickSettingsOverlay: @Composable (modifier: Modifier) -> Unit,
    debugOverlay: @Composable (
        modifier: Modifier,
        extraButtons: Array<@Composable () -> Unit>?
    ) -> Unit,
    debugVisibilityWrapper: (@Composable (@Composable () -> Unit) -> Unit),
    screenFlashOverlay: @Composable (modifier: Modifier) -> Unit,
    snackBar: @Composable (modifier: Modifier, snackbarHostState: SnackbarHostState) -> Unit
) {
    PreviewLayout(
        modifier = modifier,
        aspectRatio = currentAspectRatio,
        viewfinder = viewfinder,
        gridOverlay = { GridOverlay(modifier = it, isEnabled = isGridEnabled) },
        captureButton = captureButton,
        imageWell = imageWell,
        flipCameraButton = flipCameraButton,
        zoomLevelDisplay = zoomLevelDisplay,
        elapsedTimeDisplay = elapsedTimeDisplay,
        quickSettingsButton = quickSettingsButton,
        captureModeToggle = captureModeToggle,
        quickSettingsOverlay = quickSettingsOverlay,
        indicatorRow = { modifier ->
            // Pixel-style top bar - flash indicator left, settings button right
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Flash indicator (clickable to cycle modes)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onFlashClick() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    flashModeIndicator(Modifier.alpha(0.8f))
                }
                
                // Right: Settings button
                quickSettingsButton(Modifier)
            }
        },
        debugOverlay = { modifier ->
            debugOverlay(
                modifier,
                arrayOf(
                    { audioToggleButton(Modifier) },
                    { pauseToggleButton(Modifier) }
                )
            )
        },
        debugVisibilityWrapper = debugVisibilityWrapper,
        screenFlashOverlay = screenFlashOverlay,
        snackBar = snackBar
    )
}

@Preview
@Composable
private fun ContentScreenPreview() {
    MaterialTheme {
        ContentScreen(
            captureUiState = FAKE_PREVIEW_UI_STATE_READY,
            screenFlashUiState = ScreenFlashUiState(),
            surfaceRequest = null
        )
    }
}

@Preview
@Composable
private fun ContentScreen_Standard_Idle() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        ContentScreen(
            captureUiState = FAKE_PREVIEW_UI_STATE_READY.copy(),
            screenFlashUiState = ScreenFlashUiState(),
            surfaceRequest = null
        )
    }
}

@Preview
@Composable
private fun ContentScreen_ImageOnly_Idle() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        ContentScreen(
            captureUiState = FAKE_PREVIEW_UI_STATE_READY.copy(
                captureButtonUiState = CaptureButtonUiState.Enabled.Idle(CaptureMode.IMAGE_ONLY)
            ),
            screenFlashUiState = ScreenFlashUiState(),
            surfaceRequest = null
        )
    }
}

@Preview
@Composable
private fun ContentScreen_VideoOnly_Idle() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        ContentScreen(
            captureUiState = FAKE_PREVIEW_UI_STATE_READY.copy(
                captureButtonUiState = CaptureButtonUiState.Enabled.Idle(CaptureMode.VIDEO_ONLY)
            ),
            screenFlashUiState = ScreenFlashUiState(),
            surfaceRequest = null
        )
    }
}

@Preview
@Composable
private fun ContentScreen_Standard_Recording() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        ContentScreen(
            captureUiState = FAKE_PREVIEW_UI_STATE_PRESSED_RECORDING,
            screenFlashUiState = ScreenFlashUiState(),
            surfaceRequest = null
        )
    }
}

@Preview
@Composable
private fun ContentScreen_Locked_Recording() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        ContentScreen(
            captureUiState = FAKE_PREVIEW_UI_STATE_LOCKED_RECORDING,
            screenFlashUiState = ScreenFlashUiState(),
            surfaceRequest = null
        )
    }
}

private val FAKE_PREVIEW_UI_STATE_READY = CaptureUiState.Ready(
    videoRecordingState = VideoRecordingState.Inactive(),
    externalCaptureMode = ExternalCaptureMode.Standard,
    captureModeToggleUiState = CaptureModeToggleUiState.Unavailable
)

private val FAKE_PREVIEW_UI_STATE_PRESSED_RECORDING = FAKE_PREVIEW_UI_STATE_READY.copy(
    videoRecordingState = VideoRecordingState.Active.Recording(0, 0.0, 0),
    captureButtonUiState = CaptureButtonUiState.Enabled.Recording.PressedRecording,
    audioUiState = AudioUiState.Enabled.On(1.0)
)

private val FAKE_PREVIEW_UI_STATE_LOCKED_RECORDING = FAKE_PREVIEW_UI_STATE_READY.copy(
    videoRecordingState = VideoRecordingState.Active.Recording(0, 0.0, 0),
    captureButtonUiState = CaptureButtonUiState.Enabled.Recording.LockedRecording,
    audioUiState = AudioUiState.Enabled.On(1.0)
)
