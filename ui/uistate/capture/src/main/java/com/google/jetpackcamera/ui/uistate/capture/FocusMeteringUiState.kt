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
package com.google.jetpackcamera.ui.uistate.capture
import android.util.Range
import com.google.jetpackcamera.core.camera.CameraState
import com.google.jetpackcamera.core.camera.FocusState

sealed interface FocusMeteringUiState {
    val x: Float
    val y: Float
    val isFocused: Boolean
    val isLocked: Boolean
    val exposureCompensationIndex: Int
    val exposureCompensationRange: Range<Int>
    val status: Status

    enum class Status {
        RUNNING,
        SUCCESS,
        FAILURE,
        UNSPECIFIED
    }

    data class Specified(
        override val x: Float,
        override val y: Float,
        override val isFocused: Boolean,
        override val isLocked: Boolean,
        override val exposureCompensationIndex: Int,
        override val exposureCompensationRange: Range<Int>,
        override val status: Status
    ) : FocusMeteringUiState

    data object Unspecified : FocusMeteringUiState {
        override val x: Float = 0f
        override val y: Float = 0f
        override val isFocused: Boolean = false
        override val isLocked: Boolean = false
        override val exposureCompensationIndex: Int = 0
        override val exposureCompensationRange: Range<Int> = Range(0, 0)
        override val status: Status = Status.UNSPECIFIED
    }

    companion object {
        fun from(cameraState: CameraState): FocusMeteringUiState {
            val focusState = cameraState.focusState
            return when (focusState) {
                is FocusState.Specified -> {
                    Specified(
                        x = focusState.x,
                        y = focusState.y,
                        isFocused = focusState.status == FocusState.Status.SUCCESS,
                        isLocked = focusState.isLocked,
                        exposureCompensationIndex = cameraState.exposureCompensationIndex,
                        exposureCompensationRange = cameraState.exposureCompensationRange,
                        status = when (focusState.status) {
                            FocusState.Status.RUNNING -> Status.RUNNING
                            FocusState.Status.SUCCESS -> Status.SUCCESS
                            FocusState.Status.FAILURE -> Status.FAILURE
                            FocusState.Status.CANCELLED -> Status.FAILURE
                        }
                    )
                }

                else -> Unspecified
            }
        }
    }

    fun updateFrom(cameraState: CameraState): FocusMeteringUiState {
        return from(cameraState)
    }
}

