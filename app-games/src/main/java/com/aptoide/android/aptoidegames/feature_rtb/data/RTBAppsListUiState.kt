package com.aptoide.android.aptoidegames.feature_rtb.data

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlin.random.Random
import kotlin.random.nextInt

sealed class RTBAppsListUiState {
  data class Idle(val apps: List<RTBApp>) : RTBAppsListUiState()
  object Loading : RTBAppsListUiState()
  object Empty : RTBAppsListUiState()
  object NoConnection : RTBAppsListUiState()
  object Error : RTBAppsListUiState()
}

class RTBAppsListUiStateProvider : PreviewParameterProvider<RTBAppsListUiState> {
  override val values: Sequence<RTBAppsListUiState> = sequenceOf(
    RTBAppsListUiState.Idle(List(Random.nextInt(1..12)) { randomRTBApp }),
    RTBAppsListUiState.Loading,
    RTBAppsListUiState.Empty,
    RTBAppsListUiState.NoConnection,
    RTBAppsListUiState.Error
  )
}

val previewRTBAppsListIdleState
  get() = RTBAppsListUiState.Idle(List(Random.nextInt(1..12)) { randomRTBApp })
