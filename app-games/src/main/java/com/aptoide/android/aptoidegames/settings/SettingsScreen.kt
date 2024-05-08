package com.aptoide.android.aptoidegames.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.RadioButton
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.SupportActivity
import com.aptoide.android.aptoidegames.home.AppThemeViewModel
import com.aptoide.android.aptoidegames.network.presentation.NetworkPreferencesViewModel
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.gray2
import com.aptoide.android.aptoidegames.theme.gray7
import com.aptoide.android.aptoidegames.theme.pinkishOrange
import com.aptoide.android.aptoidegames.theme.pinkishOrangeLight
import com.aptoide.android.aptoidegames.theme.richOrange
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import cm.aptoide.pt.aptoide_ui.animations.animatedComposable
import cm.aptoide.pt.extensions.PreviewAll
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.UrlActivity
import com.aptoide.android.aptoidegames.terms_and_conditions.ppUrl
import com.aptoide.android.aptoidegames.terms_and_conditions.tcUrl

const val settingsRoute = "settings"

fun NavGraphBuilder.settingsScreen(
  navigateBack: () -> Unit,
) = animatedComposable(settingsRoute) {
  val context = LocalContext.current
  val networkPreferencesViewModel = hiltViewModel<NetworkPreferencesViewModel>()
  val downloadOnlyOverWifi by networkPreferencesViewModel.downloadOnlyOverWifi.collectAsState()

  SettingsViewContent(
    title = stringResource(R.string.settings_title),
    downloadOnlyOverWifi = downloadOnlyOverWifi,
    verName = BuildConfig.VERSION_NAME,
    verCode = BuildConfig.VERSION_CODE,
    toggleDownloadOnlyOverWifi = { isChecked ->
      networkPreferencesViewModel.setDownloadOnlyOverWifi(isChecked)
    },
    onPrivacyPolicyClick = { UrlActivity.open(context, context.ppUrl) },
    onTermsConditionsClick = { UrlActivity.open(context, context.tcUrl) },
    sendFeedback = {
      SupportActivity.open(context, "feedback")
    },
    navigateBack = navigateBack,
  )
}

@Composable
fun SettingsViewContent(
  title: String = "Settings",
  downloadOnlyOverWifi: Boolean = true,
  verName: String = "1.2.3",
  verCode: Int = 123,
  toggleDownloadOnlyOverWifi: (Boolean) -> Unit = {},
  sendFeedback: () -> Unit = {},
  copyInfo: () -> Unit = {},
  onPrivacyPolicyClick: () -> Unit = {},
  onTermsConditionsClick: () -> Unit = {},
  navigateBack: () -> Unit = {},
) {
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
      SettingsSection(
        title = stringResource(R.string.settings_general_title)
      ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
          SettingsSwitchItem(
            title = stringResource(R.string.wifi_settings_title),
            enabled = downloadOnlyOverWifi,
            onToggle = toggleDownloadOnlyOverWifi
          )
        }
      }
      SettingsSectionDivider()
      SettingsSection(
        title = stringResource(R.string.settings_support)
      ) {

        val copyText = stringResource(R.string.button_copy_title)
        val hardwareSpecsText = stringResource(R.string.settings_about_hardware_title)

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
          SettingsCaretItem(
            title = stringResource(R.string.settings_feedback_button),
            onClick = sendFeedback
          )
          Text(
            text = stringResource(R.string.settings_about_version, verName, verCode),
            style = AppTheme.typography.headlineTitleTextSecondary,
            modifier = Modifier
              .defaultMinSize(minHeight = 48.dp)
              .wrapContentHeight()
              .padding(horizontal = 8.dp, vertical = 8.dp)
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clickable(onClick = copyInfo)
              .defaultMinSize(minHeight = 48.dp)
              .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
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
              style = AppTheme.typography.headlineTitleTextSecondary,
            )
            Text(
              text = copyText,
              style = AppTheme.typography.buttonTextLight,
              color = richOrange,
              modifier = Modifier
                .clickable(onClick = copyInfo)
                .minimumInteractiveComponentSize()
                .wrapContentHeight(),
            )
          }
        }
      }
      SettingsSectionDivider()
      SettingsSection(
        title = stringResource(R.string.settings_legal_title)
      ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
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
    style = AppTheme.typography.headlineTitleText,
    modifier = Modifier.padding(start = 24.dp, top = 16.dp)
  )
  subTitle?.let {
    Text(
      text = it,
      style = AppTheme.typography.bodyCopySmallBold,
      modifier = Modifier.padding(start = 24.dp)
    )
  }
  Divider(
    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 24.dp),
    color = AppTheme.colors.moreAppsViewSeparatorColor
  )
}

@Composable
fun SettingsSectionDivider() {
  Divider(
    modifier = Modifier.padding(start = 16.dp, end = 24.dp, top = 12.dp),
    color = AppTheme.colors.moreAppsViewSeparatorColor
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
      .padding(start = 8.dp)
      .minimumInteractiveComponentSize()
      .clearAndSetSemantics {
        contentDescription = title
      }
  ) {
    Text(
      text = title,
      modifier = Modifier.weight(weight = 1f),
      style = AppTheme.typography.headlineTitleTextSecondary,
    )
    Switch(
      checked = enabled,
      onCheckedChange = onToggle,
      colors = SwitchDefaults.colors(
        checkedThumbColor = pinkishOrange,
        checkedTrackColor = pinkishOrangeLight,
        uncheckedThumbColor = gray7,
        uncheckedTrackColor = gray2
      )
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
      .clickable(onClick = onClick)
      .defaultMinSize(minHeight = 48.dp)
      .fillMaxWidth()
      .padding(horizontal = 8.dp, vertical = 8.dp)
  ) {
    if (subtitle == null) {
      Text(
        text = title,
        modifier = Modifier.weight(weight = 1f),
        style = AppTheme.typography.headlineTitleTextSecondary,
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
          style = AppTheme.typography.headlineTitleTextSecondary,
        )
        Text(
          text = subtitle,
          style = AppTheme.typography.bodyCopyXS,
        )
      }
    }
    Image(imageVector = AppTheme.icons.CaretRight, contentDescription = null)
  }
}

@PreviewAll
@Composable
fun SettingsScreenPreview(
  @PreviewParameter(SettingsScreenStateProvider::class)
  state: Boolean?,
) {
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

class SettingsScreenStateProvider : PreviewParameterProvider<Boolean?> {
  override val values: Sequence<Boolean?> = sequenceOf(null, false, true)
}
