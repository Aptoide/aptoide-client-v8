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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.SupportActivity
import com.aptoide.android.aptoidegames.UrlActivity
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.design_system.AptoideGamesSwitch
import com.aptoide.android.aptoidegames.design_system.PrimarySmallOutlinedButton
import com.aptoide.android.aptoidegames.drawables.icons.getAptoideLogo
import com.aptoide.android.aptoidegames.drawables.icons.getForward
import com.aptoide.android.aptoidegames.network.presentation.NetworkPreferencesViewModel
import com.aptoide.android.aptoidegames.terms_and_conditions.btUrl
import com.aptoide.android.aptoidegames.terms_and_conditions.ppUrl
import com.aptoide.android.aptoidegames.terms_and_conditions.tcUrl
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

const val settingsRoute = "settings"

fun settingsScreen(showSnack: (String) -> Unit) = ScreenData(
  route = settingsRoute,
) { _, _, navigateBack ->
  val context = LocalContext.current
  val genericAnalytics = rememberGenericAnalytics()
  val networkPreferencesViewModel = hiltViewModel<NetworkPreferencesViewModel>()
  val downloadOnlyOverWifi by networkPreferencesViewModel.downloadOnlyOverWifi.collectAsState()
  val deviceInfo = rememberDeviceInfo()
  val clipboardManager: ClipboardManager = LocalClipboardManager.current
  val copiedMessage = stringResource(R.string.settings_copied_to_clipboard_message)

  SettingsViewContent(
    title = stringResource(R.string.settings_title),
    downloadOnlyOverWifi = downloadOnlyOverWifi,
    verName = BuildConfig.VERSION_NAME,
    verCode = BuildConfig.VERSION_CODE,
    toggleDownloadOnlyOverWifi = { isChecked ->
      networkPreferencesViewModel.setDownloadOnlyOverWifi(isChecked)
      if (isChecked) {
        genericAnalytics.sendDownloadOverWifiEnabled()
      } else {
        genericAnalytics.sendDownloadOverWifiDisabled()
      }
    },
    onPrivacyPolicyClick = { UrlActivity.open(context, ppUrl) },
    onTermsConditionsClick = { UrlActivity.open(context, tcUrl) },
    onContactSupportClick = {
      genericAnalytics.sendPaymentSupportClicked()
      SupportActivity.openForSupport(context)
    },
    sendFeedback = {
      genericAnalytics.sendSendFeedbackClicked()
      SupportActivity.openForFeedBack(context)
    },
    onBillingTermsClick = { UrlActivity.open(context, btUrl) },
    copyInfo = {
      clipboardManager.setText(deviceInfo)
      showSnack(copiedMessage)
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
  onContactSupportClick: () -> Unit = {},
  onBillingTermsClick: () -> Unit = {},
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
      SettingsSection(title = stringResource(id = R.string.settings_billing_terms_body)) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
          SettingsCaretItem(
            title = stringResource(id = R.string.settings_payments_title),
            onClick = onContactSupportClick
          )
          SettingsCaretItem(
            title = stringResource(id = R.string.settings_contact_support_body),
            onClick = onBillingTermsClick
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
            style = AGTypography.InputsM,
            modifier = Modifier
              .defaultMinSize(minHeight = 48.dp)
              .wrapContentHeight()
              .padding(horizontal = 8.dp, vertical = 8.dp),
            color = Palette.GreyLight
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
      Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier
            .padding(start = 8.dp, top = 8.dp, bottom = 4.dp)
            .fillMaxWidth()
        ) {
          Text(
            text = stringResource(id = R.string.powered_by_title),
            style = AGTypography.Body,
            color = Palette.GreyLight
          )
        }
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier
            .padding(start = 8.dp, bottom = 33.dp)
            .fillMaxWidth()
        ) {
          Image(
            imageVector = getAptoideLogo(Palette.White),
            contentDescription = null,
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
    style = AGTypography.Title,
    modifier = Modifier.padding(start = 24.dp, top = 16.dp),
    color = Palette.White
  )
  subTitle?.let {
    Text(
      text = it,
      style = AGTypography.SmallGames,
      modifier = Modifier.padding(start = 24.dp),
      color = Palette.GreyLight
    )
  }
}

@Composable
fun SettingsSectionDivider() {
  Divider(
    modifier = Modifier.padding(top = 12.dp),
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
      .padding(start = 8.dp)
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
      .clickable(onClick = onClick)
      .defaultMinSize(minHeight = 48.dp)
      .fillMaxWidth()
      .padding(horizontal = 8.dp, vertical = 8.dp)
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
      modifier = Modifier
        .size(32.dp)
    )
  }
}

@PreviewDark
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
