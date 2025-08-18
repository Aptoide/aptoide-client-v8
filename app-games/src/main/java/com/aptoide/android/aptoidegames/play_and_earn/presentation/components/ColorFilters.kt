package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Downloading
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Installing
import cm.aptoide.pt.download_view.presentation.DownloadUiState.ReadyToInstall
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Uninstalling
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Waiting

@Composable
fun rememberDownloadGraphicFilter(state: DownloadUiState?): ColorFilter? {
  val colorFilter by remember(state) {
    derivedStateOf {
      when (state) {
        is Waiting, is Downloading, is ReadyToInstall, is Installing, is Uninstalling,
          -> ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })

        else -> null
      }
    }
  }
  return colorFilter
}
