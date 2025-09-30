package com.aptoide.android.aptoidegames.toolbar

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGeneralAnalytics
import com.aptoide.android.aptoidegames.drawables.icons.getNotificationBell
import com.aptoide.android.aptoidegames.drawables.icons.getProfileNoAccountIcon
import com.aptoide.android.aptoidegames.notifications.NotificationsPermissionRequester
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in.rememberUserInfo
import com.aptoide.android.aptoidegames.settings.settingsRoute
import com.aptoide.android.aptoidegames.theme.Palette
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("InlinedApi")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppGamesToolBar(
  navigate: (String) -> Unit,
  goBackHome: () -> Unit,
) {
  val generalAnalytics = rememberGeneralAnalytics()

  var showNotificationsDialog by remember { mutableStateOf(false) }

  val notificationsPermissionState = rememberPermissionState(
    Manifest.permission.POST_NOTIFICATIONS
  )

  val onNotificationsClick = { showNotificationsDialog = true }

  val onProfileClick = {
    generalAnalytics.sendMenuClick("settings")
    navigate(settingsRoute)
  }
  val onDismissPermissionRequesterDialog = {
    showNotificationsDialog = false
  }

  AppGamesToolBar(
    notificationsPermissionState = notificationsPermissionState.status.isGranted,
    onLogoClick = goBackHome,
    onNotificationsClick = onNotificationsClick,
    onProfileClick = onProfileClick,
  )

  NotificationsPermissionRequester(
    showDialog = showNotificationsDialog,
    onDismiss = { onDismissPermissionRequesterDialog() },
    onPermissionResult = {}
  )
}

@Composable
private fun AppGamesToolBar(
  notificationsPermissionState: Boolean,
  onLogoClick: () -> Unit,
  onNotificationsClick: () -> Unit,
  onProfileClick: () -> Unit,
) {
  val userInfo = rememberUserInfo()

  TopAppBar(
    backgroundColor = Palette.Black,
    elevation = Dp(0f),
    content = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Image(
          imageVector = BuildConfig.FLAVOR.getToolBarLogo(Palette.Primary),
          contentDescription = null,
          modifier = Modifier
            .padding(vertical = 12.dp)
            .height(20.dp)
            .clickable(
              // remove ripple effect
              interactionSource = remember { MutableInteractionSource() },
              indication = null,
              onClick = onLogoClick,
            )
            .minimumInteractiveComponentSize(),
          contentScale = ContentScale.FillHeight
        )
        Row(modifier = Modifier.wrapContentWidth()) {
          if (!notificationsPermissionState) {
            IconButton(onClick = onNotificationsClick) {
              Icon(
                imageVector = getNotificationBell(
                  bellColor = Palette.White,
                  notificationColor = Palette.Secondary
                ),
                contentDescription = stringResource(R.string.notifications_context_title),
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
              )
            }
          }
          IconButton(onClick = onProfileClick) {
            userInfo?.profilePicture?.let {
              AptoideAsyncImage(
                data = it,
                contentDescription = null,
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
              )
            } ?: Icon(
              imageVector = getProfileNoAccountIcon(),
              contentDescription = null,
              tint = Color.Unspecified
            )
          }
        }
      }
    }
  )
}

@Composable
fun SimpleAppGamesToolbar() {
  TopAppBar(
    backgroundColor = Palette.Black,
    elevation = Dp(0f),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth()
    ) {
      Image(
        imageVector = BuildConfig.FLAVOR.getToolBarLogo(Palette.Primary),
        contentDescription = null,
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@PreviewDark
@Composable
private fun AppGamesToolBarPreview() {
  AppGamesToolBar(
    notificationsPermissionState = false,
    onLogoClick = {},
    onNotificationsClick = {},
    onProfileClick = {},
  )
}
