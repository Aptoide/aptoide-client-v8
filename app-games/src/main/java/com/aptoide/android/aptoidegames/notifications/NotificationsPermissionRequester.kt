package com.aptoide.android.aptoidegames.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.hasNotificationsPermission
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.design_system.PrimaryTextButton
import com.aptoide.android.aptoidegames.drawables.icons.getNotificationsPermissionIcon
import com.aptoide.android.aptoidegames.notifications.presentation.rememberNotificationsAnalytics
import com.aptoide.android.aptoidegames.permissions.notifications.NotificationsPermissionViewModel
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

@SuppressLint("InlinedApi")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationsPermissionRequester(
  showDialog: Boolean,
  onDismiss: () -> Unit,
  onPermissionResult: (Boolean) -> Unit
) {
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  val notificationsPermissionViewModel = hiltViewModel<NotificationsPermissionViewModel>()
  val notificationsAnalytics = rememberNotificationsAnalytics()

  val onResult: (Boolean) -> Unit = { isGranted ->
    onPermissionResult(isGranted)

    if (isGranted) {
      notificationsAnalytics.sendNotificationOptIn()
    } else {
      notificationsAnalytics.sendNotificationOptOut()
    }
  }

  val notificationsPermissionState = rememberPermissionState(
    permission = Manifest.permission.POST_NOTIFICATIONS
  ) { onResult(it) }

  val intentLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult(),
    onResult = { _ ->
      //Permission is checked directly to avoid relying on the intent result.
      onResult(context.hasNotificationsPermission())
    }
  )

  if (showDialog) {
    NotificationPermissionDialog(
      onDismissDialog = { continueClicked ->
        if (continueClicked) {
          scope.launch {
            val canRequestPermissions =
              notificationsPermissionViewModel.canRequestPermission(notificationsPermissionState.status.shouldShowRationale)

            if (canRequestPermissions) {
              notificationsPermissionState.launchPermissionRequest()
            } else {
              intentLauncher.launch(
                Intent().apply {
                  action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                  putExtra("app_package", context.packageName)
                  putExtra("app_uid", context.applicationInfo.uid)
                  putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
                }
              )
            }
          }
        }

        onDismiss()
      }
    )
  }
}

@Composable
fun NotificationPermissionDialog(
  onDismissDialog: (Boolean) -> Unit,
) {
  val notificationsAnalytics = rememberNotificationsAnalytics()

  Dialog(
    onDismissRequest = { onDismissDialog(false) },
    properties = DialogProperties(
      dismissOnClickOutside = false,
      usePlatformDefaultWidth = false
    ),
  ) {
    val onContinueClick: () -> Unit = {
      notificationsAnalytics.sendGetNotifiedContinueClick()
      onDismissDialog(true)
    }
    val onCancelClick: () -> Unit = { onDismissDialog(false) }
    DialogContent(onContinueClick, onCancelClick)
  }
}

@Preview
@Composable
fun DialogContentPreview() {
  DialogContent({}, {})
}

@Composable
fun DialogContent(
  onContinueClick: () -> Unit,
  onCancelClick: () -> Unit,
) {
  Box(
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .width(328.dp)
      .wrapContentHeight()
      .background(color = Palette.GreyDark),
    contentAlignment = Alignment.Center
  ) {
    Column(
      verticalArrangement = Arrangement.SpaceEvenly,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .wrapContentHeight()
        .padding(horizontal = 24.dp)
    ) {
      Image(
        imageVector = getNotificationsPermissionIcon(
          controllerColor = Palette.Primary,
          bellColor = Palette.White,
          notificationColor = Palette.Secondary
        ),
        contentDescription = null,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 40.dp)
      )
      Text(
        text = stringResource(R.string.notifications_context_title),
        style = AGTypography.Title,
        color = Palette.White,
        modifier = Modifier.padding(bottom = 8.dp)
      )
      Text(
        text = stringResource(R.string.notifications_context_body),
        style = AGTypography.SubHeadingS,
        color = Palette.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .padding(bottom = 24.dp)
          .fillMaxWidth()
      )
      PrimaryButton(
        onClick = onContinueClick,
        modifier = Modifier
          .fillMaxWidth()
          .padding(),
        title = stringResource(R.string.continue_button)
      )
      PrimaryTextButton(
        onClick = onCancelClick,
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        text = stringResource(id = R.string.cancel_button)
      )
    }
  }
}
