package cm.aptoide.pt.app_games.toolbar

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.R
import cm.aptoide.pt.app_games.settings.settingsRoute
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.extensions.PreviewAll

@PreviewAll
@Composable
private fun AppGamesToolBarPreview() {
  AppGamesToolBar(
    showMenu = false,
    onLogoClick = {},
    onShowMenuClick = {},
    onDropDownSettingsClick = {},
    onDropDownTermsConditionsClick = {},
    onDropDownPrivacyPolicyClick = {},
    onDropDownDismissRequest = {},
  )
}

@SuppressLint("InlinedApi")
@Composable
fun AppGamesToolBar(
  navigate: (String) -> Unit,
  goBackHome: () -> Unit,
) {
  var showMenu by remember { mutableStateOf(false) }

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

  AppGamesToolBar(
    showMenu = showMenu,
    onLogoClick = goBackHome,
    onShowMenuClick = onShowMenuClick,
    onDropDownSettingsClick = onDropDownSettingsClick,
    onDropDownTermsConditionsClick = onDropDownTermsConditionsClick,
    onDropDownPrivacyPolicyClick = onDropDownPrivacyPolicyClick,
    onDropDownDismissRequest = onDropDownDismissRequest,
  )
}

@Composable
private fun AppGamesToolBar(
  showMenu: Boolean,
  onLogoClick: () -> Unit,
  onShowMenuClick: () -> Unit,
  onDropDownSettingsClick: () -> Unit,
  onDropDownTermsConditionsClick: () -> Unit,
  onDropDownPrivacyPolicyClick: () -> Unit,
  onDropDownDismissRequest: () -> Unit,
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
}
