package com.aptoide.android.aptoidegames.notifications

import android.Manifest
import android.Manifest.permission
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.aptoide.android.aptoidegames.MainActivity
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.pureWhite
import com.aptoide.android.aptoidegames.theme.richOrange
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

  NotificationPermissionDialog { continueClicked ->
    if (continueClicked) {
      val shouldShowRationale = notificationsPermissionState.status.shouldShowRationale
      notificationsPermissionViewModel.requestPermission(shouldShowRationale) {
        try {
          (context as MainActivity).requestPermissionLauncher.launch(permission.POST_NOTIFICATIONS)
        } catch (exception: Exception) {
          Timber.e(exception)
        }
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
    val onContinueClick: () -> Unit = {
      onDismissDialog(true)
    }
    DialogContent(onContinueClick)
  }
}

@Preview
@Composable
fun DialogContentPreview() {
  DialogContent {}
}

@Composable
fun DialogContent(onContinueClick: () -> Unit) {
  Box(
    modifier = Modifier
      .padding(horizontal = 24.dp)
      .fillMaxWidth()
      .wrapContentHeight()
      .defaultMinSize(minHeight = 261.dp)
      .clip(RoundedCornerShape(24.dp))
      .paint(
        painter = painterResource(id = R.drawable.notification_permission_bg),
        contentScale = ContentScale.Crop,
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      verticalArrangement = Arrangement.SpaceEvenly,
      modifier = Modifier
        .wrapContentHeight()
    ) {
      Text(
        text = stringResource(R.string.notifications_context_title),
        style = AppTheme.typography.headlineTitleText,
        color = pureWhite,
        modifier = Modifier.padding(top = 35.dp, start = 24.dp, bottom = 16.dp, end = 24.dp)
      )
      Text(
        text = stringResource(R.string.notifications_context_body),
        style = AppTheme.typography.bodyCopySmall,
        color = pureWhite,
        modifier = Modifier.padding(start = 24.dp, bottom = 24.dp, end = 32.dp)
      )
      Button(
        onClick = onContinueClick,
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier
          .padding(start = 24.dp, end = 8.dp, bottom = 40.dp)
          .defaultMinSize(minWidth = 88.dp)
          .wrapContentWidth()
          .height(32.dp),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = richOrange)
      ) {
        Text(
          text = stringResource(R.string.continue_button),
          maxLines = 1,
          style = AppTheme.typography.buttonTextMedium,
          color = pureWhite
        )
      }
    }
  }
}
