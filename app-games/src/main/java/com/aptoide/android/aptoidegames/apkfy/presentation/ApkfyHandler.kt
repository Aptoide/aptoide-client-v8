package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.aptoide.android.aptoidegames.permissions.InstallPermissionsViewModel

@Composable
fun ApkfyHandler(navigate: (String) -> Unit) {
  val apkfyState = rememberApkfyState()
  var apkfyShown by rememberSaveable { mutableStateOf(false) }
  val installPermissionsViewModel = hiltViewModel<InstallPermissionsViewModel>()


  LaunchedEffect(apkfyState, apkfyShown) {
    if (apkfyState != null && !apkfyShown) {
      installPermissionsViewModel.requestInstallPermissions()
      when (apkfyState) {
        is ApkfyUiState.Default,
        is ApkfyUiState.Baseline -> {
          navigate(apkfyScreenRoute)
        }

        is ApkfyUiState.VariantA -> {
          navigate(detailedApkfyRoute)
        }

        is ApkfyUiState.RobloxBaseline -> {
          navigate(detailedApkfyRoute)
        }

        is ApkfyUiState.RobloxVariantA -> {
          navigate(robloxApkfyRoute)
        }
      }

      apkfyShown = true
    }
  }
}
