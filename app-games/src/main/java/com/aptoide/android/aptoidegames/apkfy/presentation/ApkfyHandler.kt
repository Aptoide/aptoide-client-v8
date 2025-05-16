package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.aptoide.android.aptoidegames.BottomSheetContent
import com.aptoide.android.aptoidegames.apkfy.ApkfyBottomSheetContent

@Composable
fun ApkfyHandler(
  showBottomSheet: (BottomSheetContent?) -> Unit
) {
  val apkfyState = rememberApkfyState()
  var apkfyShown by remember { mutableStateOf(false) }

  LaunchedEffect(apkfyState, apkfyShown) {
    if (apkfyState != null && !apkfyShown) {
      showBottomSheet(ApkfyBottomSheetContent(apkfyState))
      apkfyShown = true
    }
  }
}
