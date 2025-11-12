package com.aptoide.android.aptoidegames.settings

import android.content.ClipData
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.extensions.multiTap
import cm.aptoide.pt.feature_updates.di.rememberAutoUpdate
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.SupportActivity
import com.aptoide.android.aptoidegames.UrlActivity
import com.aptoide.android.aptoidegames.design_system.AptoideGamesSwitch
import com.aptoide.android.aptoidegames.design_system.PrimarySmallOutlinedButton
import com.aptoide.android.aptoidegames.developer.rememberAGDeveloperOptions
import com.aptoide.android.aptoidegames.drawables.icons.getAptoideLogo
import com.aptoide.android.aptoidegames.drawables.icons.getForward
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getStartEarningIcon
import com.aptoide.android.aptoidegames.firebase.rememberFirebaseToken
import com.aptoide.android.aptoidegames.network.presentation.NetworkPreferencesViewModel
import com.aptoide.android.aptoidegames.play_and_earn.di.rememberWalletAddress
import com.aptoide.android.aptoidegames.play_and_earn.domain.UserInfo
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaESmallTextButton
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in.GoogleSignInViewModel
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in.playAndEarnSignInRoute
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in.rememberUserInfo
import com.aptoide.android.aptoidegames.terms_and_conditions.ppUrl
import com.aptoide.android.aptoidegames.terms_and_conditions.tcUrl
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import kotlinx.coroutines.launch

const val settingsRoute = "settings"

fun settingsScreen(showSnack: (String) -> Unit) = ScreenData(
  route = settingsRoute,
) { _, navigate, navigateBack ->
  val context = LocalContext.current
  val settingsAnalytics = rememberSettingsAnalytics()
  val networkPreferencesViewModel = hiltViewModel<NetworkPreferencesViewModel>()
  val downloadOnlyOverWifi by networkPreferencesViewModel.downloadOnlyOverWifi.collectAsState()
  val (autoUpdateGames, toggleAutoUpdateGames) = rememberAutoUpdate()
  val deviceInfo = rememberDeviceInfo()
  val clipboard = LocalClipboard.current
  val coroutineScope = rememberCoroutineScope()
  val copiedMessage = stringResource(R.string.settings_copied_to_clipboard_message)
  val signInViewModel = hiltViewModel<GoogleSignInViewModel>()

  SettingsViewContent(
    title = stringResource(R.string.settings_title),
    downloadOnlyOverWifi = downloadOnlyOverWifi,
    autoUpdateGames = autoUpdateGames,
    verName = BuildConfig.VERSION_NAME,
    verCode = BuildConfig.VERSION_CODE,
    toggleDownloadOnlyOverWifi = { isChecked ->
      networkPreferencesViewModel.setDownloadOnlyOverWifi(isChecked)
      if (isChecked) {
        settingsAnalytics.sendDownloadOverWifiEnabled()
      } else {
        settingsAnalytics.sendDownloadOverWifiDisabled()
      }
    },
    toggleAutoUpdateGames = { isChecked ->
      toggleAutoUpdateGames(isChecked)
    },
    onPrivacyPolicyClick = { UrlActivity.open(context, ppUrl) },
    onTermsConditionsClick = { UrlActivity.open(context, tcUrl) },
    sendFeedback = {
      settingsAnalytics.sendSendFeedbackClicked()
      SupportActivity.openForFeedBack(context)
    },
    copyInfo = {
      coroutineScope.launch {
        clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Device info", deviceInfo)))
      }
      showSnack(copiedMessage)
    },
    onLetsGoClick = { navigate(playAndEarnSignInRoute) },
    onSignOutClick = signInViewModel::signOut,
    navigateBack = navigateBack,
  )
}

@Composable
fun SettingsViewContent(
  title: String = "Settings",
  downloadOnlyOverWifi: Boolean = true,
  autoUpdateGames: Boolean? = true,
  verName: String = "1.2.3",
  verCode: Int = 123,
  toggleDownloadOnlyOverWifi: (Boolean) -> Unit = {},
  toggleAutoUpdateGames: (Boolean) -> Unit = {},
  sendFeedback: () -> Unit = {},
  copyInfo: () -> Unit = {},
  onPrivacyPolicyClick: () -> Unit = {},
  onTermsConditionsClick: () -> Unit = {},
  onLetsGoClick: () -> Unit = {},
  onSignOutClick: () -> Unit = {},
  navigateBack: () -> Unit = {},
) {
  val context = LocalContext.current
  val (areAGDeveloperOptionsEnabled, toggleAGDeveloperOptions) = rememberAGDeveloperOptions()
  val settingsAnalytics = rememberSettingsAnalytics()

  val userInfo = rememberUserInfo()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.TopCenter)
  ) {
    AppGamesTopBar(navigateBack = navigateBack, title = title)
    Column(
      modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(bottom = 40.dp)
    ) {
      CurrentUserHeader(
        userInfo = userInfo,
        modifier = Modifier
          .padding(top = 16.dp)
          .align(Alignment.CenterHorizontally),
        onLetsGoClick = onLetsGoClick
      )
      SettingsSection(
        title = stringResource(R.string.settings_general_title)
      ) {
        Column(
          modifier = Modifier.padding(horizontal = 16.dp),
        ) {
          SettingsSwitchItem(
            title = stringResource(R.string.wifi_settings_title),
            enabled = downloadOnlyOverWifi,
            onToggle = toggleDownloadOnlyOverWifi
          )
          if (autoUpdateGames != null) {
            SettingsSwitchItem(
              title = stringResource(R.string.settings_auto_update_button),
              enabled = autoUpdateGames,
              onToggle = toggleAutoUpdateGames
            )
          }
        }
      }
      SettingsSectionDivider()
      SettingsSection(
        title = stringResource(R.string.settings_support)
      ) {
        val copyText = stringResource(R.string.button_copy_title)
        val hardwareSpecsText = stringResource(R.string.settings_about_hardware_title)

        Column(
          modifier = Modifier.padding(horizontal = 16.dp),
        ) {
          SettingsCaretItem(
            title = stringResource(R.string.settings_feedback_button),
            onClick = sendFeedback
          )
          Text(
            text = stringResource(R.string.settings_about_version, verName, verCode),
            style = AGTypography.InputsM,
            modifier = Modifier
              .defaultMinSize(minHeight = 48.dp)
              .wrapContentHeight()
              .multiTap(enabled = !areAGDeveloperOptionsEnabled) {
                toggleAGDeveloperOptions(true)
                Toast.makeText(context, "Developer options activated!", Toast.LENGTH_SHORT).show()
                settingsAnalytics.sendAGDevOptionsEnabled()
              },
            color = Palette.GreyLight
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clickable(onClick = copyInfo)
              .defaultMinSize(minHeight = 48.dp)
              .clearAndSetSemantics {
                contentDescription = hardwareSpecsText
                onClick(label = copyText) {
                  copyInfo()
                  true
                }
              }
          ) {
            Text(
              text = hardwareSpecsText,
              modifier = Modifier.weight(weight = 1f),
              style = AGTypography.InputsM,
              color = Palette.GreyLight
            )
            PrimarySmallOutlinedButton(
              onClick = copyInfo,
              title = copyText
            )
          }
        }
      }
      SettingsSectionDivider()
      SettingsSection(
        title = stringResource(R.string.settings_legal_title)
      ) {
        Column(
          modifier = Modifier.padding(horizontal = 16.dp),
        ) {
          SettingsCaretItem(
            title = stringResource(R.string.overflow_menu_privacy_policy),
            onClick = onPrivacyPolicyClick
          )
          SettingsCaretItem(
            title = stringResource(R.string.overflow_menu_terms_conditions),
            onClick = onTermsConditionsClick
          )
        }
      }
      if (userInfo != null) {
        LogoutItem(onClick = onSignOutClick)
      }
      AnimatedVisibility(
        visible = areAGDeveloperOptionsEnabled,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Column {
          SettingsSectionDivider()
          AGDeveloperOptionsSection()
        }
      }
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          modifier = Modifier.padding(bottom = 4.dp),
          text = stringResource(id = R.string.powered_by_title),
          style = AGTypography.Body,
          color = Palette.GreyLight
        )
        Image(
          imageVector = getAptoideLogo(Palette.White),
          contentDescription = null,
        )
      }
    }
  }
}

@Composable
private fun CurrentUserHeader(
  userInfo: UserInfo?,
  modifier: Modifier = Modifier,
  onLetsGoClick: () -> Unit,
) {
  userInfo?.let {
    Column(
      modifier = modifier.fillMaxWidth(),
    ) {
      CurrentUserInfoSection(
        userInfo = it,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
      SettingsSectionDivider()
    }
  } ?: PaESettingsLetsGoCard(
    modifier = modifier.padding(horizontal = 16.dp),
    onLetsGoClick = onLetsGoClick
  )
}

@Composable
private fun CurrentUserInfoSection(
  userInfo: UserInfo,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AptoideAsyncImage(
      data = userInfo.profilePicture,
      contentDescription = null,
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
    )
    Column {
      userInfo.name?.let {
        Text(
          text = it,
          style = AGTypography.InputsL,
          color = Palette.GreyLight
        )
      }
      userInfo.email?.let {
        Text(
          text = it,
          style = AGTypography.InputsXSRegular,
          color = Palette.GreyLight
        )
      }
    }
  }
}

@Composable
fun AGDeveloperOptionsSection() {
  val firebaseToken = rememberFirebaseToken()
  val walletAddress = rememberWalletAddress()
  val clipboard = LocalClipboard.current
  val coroutineScope = rememberCoroutineScope()
  val (areAGDeveloperOptionsEnabled, toggleAGDeveloperOptions) = rememberAGDeveloperOptions()

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
  ) {
    SettingsSection(
      title = "Developer Options"
    ) {
      Column(
        modifier = Modifier.padding(horizontal = 16.dp)
      ) {
        SettingsSwitchItem(
          title = "Turn off developer options",
          enabled = areAGDeveloperOptionsEnabled,
          onToggle = toggleAGDeveloperOptions
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.defaultMinSize(minHeight = 48.dp)
        ) {
          Text(
            modifier = Modifier
              .padding(end = 16.dp)
              .wrapContentHeight(),
            text = "FCM Token",
            style = AGTypography.InputsM,
            color = Palette.GreyLight
          )

          firebaseToken?.let {
            Text(
              modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .wrapContentHeight()
                .clickable {
                  coroutineScope.launch {
                    clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("FCM token", it)))
                  }
                },
              text = it,
              style = AGTypography.InputsM,
              color = Palette.GreyLight
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier
            .defaultMinSize(minHeight = 48.dp)
            .padding(top = 8.dp)
        ) {
          Text(
            modifier = Modifier
              .padding(end = 16.dp)
              .wrapContentHeight(),
            text = "Wallet address",
            style = AGTypography.InputsM,
            color = Palette.GreyLight
          )

          walletAddress?.let {
            Text(
              modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .wrapContentHeight()
                .clickable {
                  coroutineScope.launch {
                    clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Wallet address", it)))
                  }
                },
              text = it,
              style = AGTypography.InputsM,
              color = Palette.GreyLight
            )
          }
        }
      }
    }
  }
}

@Composable
fun SettingsSection(
  title: String,
  subTitle: String? = null,
  content: @Composable () -> Unit,
) {
  SettingsSectionHeader(title, subTitle)
  content()
}

@Composable
fun SettingsSectionHeader(
  title: String,
  subTitle: String? = null,
) {
  Text(
    text = title,
    style = AGTypography.Title,
    modifier = Modifier.padding(start = 16.dp, top = 24.dp),
    color = Palette.White
  )
  subTitle?.let {
    Text(
      text = it,
      style = AGTypography.SmallGames,
      modifier = Modifier.padding(start = 16.dp),
      color = Palette.GreyLight
    )
  }
}

@Composable
fun SettingsSectionDivider() {
  Divider(
    modifier = Modifier.padding(top = 20.dp),
    color = Palette.Grey
  )
}

@Composable
fun SettingsSwitchItem(
  title: String,
  enabled: Boolean,
  onToggle: (Boolean) -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .toggleable(
        value = enabled,
        role = Role.Switch,
        onValueChange = onToggle
      )
      .defaultMinSize(minHeight = 48.dp)
      .fillMaxWidth()
      .minimumInteractiveComponentSize()
      .clearAndSetSemantics {
        contentDescription = title
      }
  ) {
    Text(
      text = title,
      modifier = Modifier.weight(weight = 1f),
      style = AGTypography.InputsM,
      color = Palette.GreyLight
    )
    AptoideGamesSwitch(
      checked = enabled,
      onCheckedChanged = onToggle
    )
  }
}

@Composable
fun SettingsCaretItem(
  title: String,
  subtitle: String? = null,
  onClick: () -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 48.dp)
      .clickable(onClick = onClick)
  ) {
    if (subtitle == null) {
      Text(
        text = title,
        modifier = Modifier.weight(weight = 1f),
        style = AGTypography.InputsM,
        color = Palette.GreyLight
      )
    } else {
      Column(
        modifier = Modifier
          .fillMaxHeight()
          .weight(weight = 1f),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = title,
          modifier = Modifier.padding(bottom = 11.dp),
          style = AGTypography.InputsM,
          color = Palette.White
        )
        Text(
          text = subtitle,
          style = AGTypography.InputsS,
          color = Palette.White
        )
      }
    }
    Image(
      imageVector = getForward(Palette.Primary),
      contentDescription = null,
      modifier = Modifier.size(32.dp)
    )
  }
}

@Composable
fun LogoutItem(
  onClick: () -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .fillMaxWidth()
      .defaultMinSize(minHeight = 48.dp)
      .clickable(onClick = onClick)
  ) {
    Text(
      text = stringResource(R.string.logout_button),
      modifier = Modifier.weight(weight = 1f),
      style = AGTypography.InputsM,
      color = Palette.Error
    )

    Image(
      imageVector = Icons.AutoMirrored.Filled.Logout,
      contentDescription = null,
      modifier = Modifier.size(22.dp),
      colorFilter = ColorFilter.tint(Palette.Error)
    )
  }
}

@Composable
fun PaESettingsLetsGoCard(
  modifier: Modifier = Modifier,
  onLetsGoClick: () -> Unit,
) {
  Box(
    modifier = modifier
      .width(328.dp)
      .background(Palette.GreyDark)
      .border(width = 2.dp, color = Palette.Blue200),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(all = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Image(
          modifier = Modifier.size(72.dp, 52.dp),
          imageVector = getStartEarningIcon(),
          contentDescription = null
        )
        Column(
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = stringResource(R.string.play_and_earn_get_started_now_title),
            style = AGTypography.InputsM,
            color = Palette.Yellow100
          )
          Text(
            text = stringResource(R.string.play_and_earn_grant_permissions_body),
            style = AGTypography.Body,
            color = Palette.White
          )
        }
      }

      PaESmallTextButton(
        title = stringResource(R.string.play_and_earn_lets_go_button),
        onClick = onLetsGoClick,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@PreviewDark
@Composable
fun SettingsScreenPreview() {
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    SettingsViewContent(
      title = "Settings",
      verName = "1.2.3",
      verCode = 123,
      sendFeedback = {},
      copyInfo = {},
      navigateBack = {},
    )
  }
}
