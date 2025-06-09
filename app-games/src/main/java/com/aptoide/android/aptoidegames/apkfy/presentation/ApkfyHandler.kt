package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun ApkfyHandler(navigate: (String) -> Unit) {
  val apkfyState = rememberApkfyState()
  var apkfyShown by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(apkfyState, apkfyShown) {
    if (apkfyState != null && !apkfyShown) {
      when (apkfyState) {
        is ApkfyUiState.Default,
        is ApkfyUiState.Baseline -> navigate(apkfyScreenRoute)

        is ApkfyUiState.VariantA -> navigate(detailedApkfyRoute)

        is ApkfyUiState.RobloxBaseline -> navigate(detailedApkfyRoute)

        is ApkfyUiState.RobloxVariantA -> navigate(robloxApkfyRoute)
      }

      apkfyShown = true
    }
  }
}
