package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.aptoide.android.aptoidegames.BottomSheetContent
import com.aptoide.android.aptoidegames.apkfy.ApkfyBottomSheetContent
import com.aptoide.android.aptoidegames.permissions.InstallPermissionsViewModel

@Composable
fun ApkfyHandler(
  showBottomSheet: (BottomSheetContent?) -> Unit,
  navigate: (String) -> Unit,
) {
  val apkfyState = rememberApkfyState()
  var apkfyShown by remember { mutableStateOf(false) }

  val installPermissionsViewModel = hiltViewModel<InstallPermissionsViewModel>()

  LaunchedEffect(apkfyState, apkfyShown) {
    if (apkfyState != null && !apkfyShown) {
      when (apkfyState) {
        is ApkfyUiState.Default,
        is ApkfyUiState.VariantA -> showBottomSheet(ApkfyBottomSheetContent(apkfyState))

        is ApkfyUiState.VariantB -> navigate(apkfyScreenRoute)

        is ApkfyUiState.VariantC -> {
          installPermissionsViewModel.requestInstallPermissions()
          showBottomSheet(ApkfyBottomSheetContent(apkfyState))
        }

        is ApkfyUiState.VariantD -> {
          installPermissionsViewModel.requestInstallPermissions()
          navigate(apkfyScreenRoute)
        }
      }
      apkfyShown = true
    }
  }
}
