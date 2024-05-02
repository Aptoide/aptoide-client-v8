package com.aptoide.android.aptoidegames.notifications

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.aptoide.android.aptoidegames.MainActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import timber.log.Timber

@SuppressLint("InlinedApi")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationsPermissionRequester(
  onDismiss: () -> Unit
) {
  val notificationsPermissionViewModel = hiltViewModel<NotificationsPermissionViewModel>()
  val context = LocalContext.current

  val notificationsPermissionState =
    rememberPermissionState(
      permission = Manifest.permission.POST_NOTIFICATIONS
    ) { }

  NotificationPermissionDialog {
    val shouldShowRationale = notificationsPermissionState.status.shouldShowRationale
    notificationsPermissionViewModel.requestPermission(shouldShowRationale) {
      try {
        (context as MainActivity).requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      } catch (exception: Exception) {
        Timber.e(exception)
      }
    }

    onDismiss()
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NotificationPermissionDialog(
  onDismissDialog: (Boolean) -> Unit,
) {

  Dialog(
    onDismissRequest = { onDismissDialog(false) },
    properties = DialogProperties(
      dismissOnClickOutside = false,
      usePlatformDefaultWidth = false
    ),
  ) {
    onDismissDialog(true)
  }
}
