package cm.aptoide.pt.app_games.toolbar

import android.Manifest.permission
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.R
import cm.aptoide.pt.app_games.notifications.NotificationsPermissionRequester
import cm.aptoide.pt.app_games.settings.settingsRoute
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.extensions.PreviewAll
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@PreviewAll
@Composable
private fun AppGamesToolBarPreview() {
  AppGamesToolBar(
    showMenu = false,
    showNotificationsDialog = false,
    notificationsPermissionState = false,
    onLogoClick = {},
    onNotificationsClick = {},
    onShowMenuClick = {},
    onDropDownSettingsClick = {},
    onDropDownTermsConditionsClick = {},
    onDropDownPrivacyPolicyClick = {},
    onDropDownDismissRequest = {},
    onDismissPermissionRequesterDialog = {},
  )
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("InlinedApi")
@Composable
fun AppGamesToolBar(
  navigate: (String) -> Unit,
  goBackHome: () -> Unit,
) {
  var showMenu by remember { mutableStateOf(false) }

  var showNotificationsDialog by remember { mutableStateOf(false) }

  val notificationsPermissionState = rememberPermissionState(
    permission.POST_NOTIFICATIONS
  )

  val onNotificationsClick = { showNotificationsDialog = true }
  val onShowMenuClick = { showMenu = !showMenu }
  val onDropDownSettingsClick = {
    showMenu = false
    navigate(settingsRoute)
  }
  val onDropDownTermsConditionsClick = {
    showMenu = false
  }
  val onDropDownPrivacyPolicyClick = {
    showMenu = false
  }
  val onDropDownDismissRequest = { showMenu = false }

  val onDismissPermissionRequesterDialog = {
    showNotificationsDialog = false
  }

  AppGamesToolBar(
    showMenu = showMenu,
    showNotificationsDialog = showNotificationsDialog,
    notificationsPermissionState = notificationsPermissionState.status.isGranted,
    onLogoClick = goBackHome,
    onNotificationsClick = onNotificationsClick,
    onShowMenuClick = onShowMenuClick,
    onDropDownSettingsClick = onDropDownSettingsClick,
    onDropDownTermsConditionsClick = onDropDownTermsConditionsClick,
    onDropDownPrivacyPolicyClick = onDropDownPrivacyPolicyClick,
    onDropDownDismissRequest = onDropDownDismissRequest,
    onDismissPermissionRequesterDialog = onDismissPermissionRequesterDialog,
  )
}

@Composable
private fun AppGamesToolBar(
  showMenu: Boolean,
  showNotificationsDialog: Boolean,
  notificationsPermissionState: Boolean,
  onLogoClick: () -> Unit,
  onNotificationsClick: () -> Unit,
  onShowMenuClick: () -> Unit,
  onDropDownSettingsClick: () -> Unit,
  onDropDownTermsConditionsClick: () -> Unit,
  onDropDownPrivacyPolicyClick: () -> Unit,
  onDropDownDismissRequest: () -> Unit,
  onDismissPermissionRequesterDialog: () -> Unit,
) {
  TopAppBar(
    backgroundColor = AppTheme.colors.background,
    elevation = Dp(0f),
    content = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Spacer(modifier = Modifier.width(48.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth()
            .weight(1f)
        ) {
          Image(
            imageVector = AppTheme.icons.ToolBarLogo,
            contentDescription = null,
            modifier = Modifier
              .clickable(
                // remove ripple effect
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onLogoClick,
              )
              .minimumInteractiveComponentSize()
              .weight(1f)
          )
        }
        Row(
          modifier = Modifier.wrapContentWidth(),
        ) {
          if (!notificationsPermissionState) {
            IconButton(onClick = onNotificationsClick) {
              Icon(
                imageVector = AppTheme.icons.NotificationBell,
                contentDescription = stringResource(R.string.notifications_context_title),
                tint = Color.Unspecified
              )
            }
          } else {
            Spacer(modifier = Modifier.width(48.dp))
          }
          Column {
            IconButton(onClick = onShowMenuClick) {
              Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.home_overflow_talkback)
              )
            }
            DropdownMenu(
              expanded = showMenu,
              onDismissRequest = onDropDownDismissRequest
            ) {
              DropdownMenuItem(onClick = onDropDownSettingsClick) {
                Text(text = stringResource(R.string.overflow_menu_settings))
              }
              DropdownMenuItem(
                onClick = onDropDownTermsConditionsClick
              ) {
                Text(text = stringResource(R.string.overflow_menu_terms_conditions))
              }
              DropdownMenuItem(
                onClick = onDropDownPrivacyPolicyClick
              ) {
                Text(text = stringResource(R.string.overflow_menu_privacy_policy))
              }
            }
          }
        }
      }
    }
  )
  if (showNotificationsDialog) {
    NotificationsPermissionRequester(onDismissPermissionRequesterDialog)
  }
}
