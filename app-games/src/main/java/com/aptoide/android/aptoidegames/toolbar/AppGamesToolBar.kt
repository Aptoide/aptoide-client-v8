package com.aptoide.android.aptoidegames.toolbar

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.UrlActivity
import com.aptoide.android.aptoidegames.drawables.icons.getMoreVert
import com.aptoide.android.aptoidegames.settings.settingsRoute
import com.aptoide.android.aptoidegames.terms_and_conditions.ppUrl
import com.aptoide.android.aptoidegames.terms_and_conditions.tcUrl
import com.aptoide.android.aptoidegames.theme.Palette

@SuppressLint("InlinedApi")
@Composable
fun AppGamesToolBar(
  navigate: (String) -> Unit,
  goBackHome: () -> Unit,
) {
  var showMenu by remember { mutableStateOf(false) }
  val context = LocalContext.current

  val onShowMenuClick = { showMenu = !showMenu }
  val onDropDownSettingsClick = {
    showMenu = false
    navigate(settingsRoute)
  }
  val onDropDownTermsConditionsClick = {
    showMenu = false
    UrlActivity.open(context, context.tcUrl)
  }
  val onDropDownPrivacyPolicyClick = {
    showMenu = false
    UrlActivity.open(context, context.ppUrl)
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
    backgroundColor = Palette.Black,
    elevation = Dp(0f),
    content = {
      Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Spacer(modifier = Modifier.width(48.dp))
        Image(
          imageVector = BuildConfig.FLAVOR.getToolBarLogo(),
          contentDescription = null,
          modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable(
              // remove ripple effect
              interactionSource = remember { MutableInteractionSource() },
              indication = null,
              onClick = onLogoClick,
            )
            .minimumInteractiveComponentSize()
            .weight(1f)
        )
        Column {
          IconButton(onClick = onShowMenuClick) {
            Icon(
              imageVector = getMoreVert(Palette.White),
              contentDescription = stringResource(R.string.home_overflow_talkback),
              tint = Palette.White,
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
        imageVector = BuildConfig.FLAVOR.getToolBarLogo(),
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
    showMenu = false,
    onLogoClick = {},
    onShowMenuClick = {},
    onDropDownSettingsClick = {},
    onDropDownTermsConditionsClick = {},
    onDropDownPrivacyPolicyClick = {},
    onDropDownDismissRequest = {},
  )
}
