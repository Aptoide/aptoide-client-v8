package cm.aptoide.pt.app_games.settings

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
import cm.aptoide.pt.app_games.R
import cm.aptoide.pt.app_games.home.AppThemeViewModel
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.app_games.theme.AptoideTheme
import cm.aptoide.pt.app_games.theme.gray2
import cm.aptoide.pt.app_games.theme.gray7
import cm.aptoide.pt.app_games.theme.pinkishOrange
import cm.aptoide.pt.app_games.theme.pinkishOrangeLight
import cm.aptoide.pt.app_games.theme.richOrange
import cm.aptoide.pt.app_games.toolbar.AppGamesTopBar
import cm.aptoide.pt.aptoide_ui.animations.animatedComposable
import cm.aptoide.pt.extensions.PreviewAll

const val settingsRoute = "settings"

fun NavGraphBuilder.settingsScreen(
  navigateBack: () -> Unit,
) = animatedComposable(settingsRoute) {
  val themeViewModel = hiltViewModel<AppThemeViewModel>()
  val isDarkTheme by themeViewModel.uiState.collectAsState()

  var showOptOutDialog by remember { mutableStateOf(false) }
  var acceptedPPAndTC by remember { mutableStateOf(true) } //Radio button state - Used for UI/UX purposes


  SettingsViewContent(
    title = stringResource(R.string.settings_title),
    isDarkTheme = isDarkTheme,
    acceptedPPAndTC = acceptedPPAndTC,
    selectSystemDefault = themeViewModel::setSystem,
    selectLight = themeViewModel::setLight,
    selectDark = themeViewModel::setDark,
    togglePPAndTC = { isChecked ->
      if (!isChecked) {
        acceptedPPAndTC = false //Turn false for UX purposes
        showOptOutDialog = true
      }
    },
    navigateBack = navigateBack,
  )
}

@Composable
fun SettingsViewContent(
  title: String = "Settings",
  downloadOnlyOverWifi: Boolean = true,
  isDarkTheme: Boolean? = null,
  acceptedPPAndTC: Boolean = true,
  verName: String = "1.2.3",
  verCode: Int = 123,
  toggleDownloadOnlyOverWifi: (Boolean) -> Unit = {},
  selectSystemDefault: () -> Unit = {},
  selectLight: () -> Unit = {},
  selectDark: () -> Unit = {},
  sendFeedback: () -> Unit = {},
  copyInfo: () -> Unit = {},
  togglePPAndTC: (Boolean) -> Unit = {},
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
          Text(
            text = stringResource(R.string.settings_theme_options_title),
            style = AppTheme.typography.headlineTitleTextSecondary,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp)
          )
          ThemeOption(
            title = stringResource(R.string.settings_theme_option_system),
            selected = isDarkTheme == null,
            onClick = selectSystemDefault
          )
          ThemeOption(
            title = stringResource(R.string.settings_theme_option_light),
            selected = isDarkTheme == false,
            onClick = selectLight
          )
          ThemeOption(
            title = stringResource(R.string.settings_theme_option_dark),
            selected = isDarkTheme == true,
            onClick = selectDark
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
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(26.dp),
            modifier = Modifier
              .defaultMinSize(minHeight = 48.dp)
              .fillMaxWidth()
              .padding(start = 8.dp)
          ) {
            Switch(
              modifier = Modifier.clearAndSetSemantics { },
              checked = acceptedPPAndTC,
              onCheckedChange = togglePPAndTC,
              colors = SwitchDefaults.colors(
                checkedThumbColor = pinkishOrange,
                checkedTrackColor = pinkishOrangeLight,
                uncheckedThumbColor = gray7,
                uncheckedTrackColor = gray2
              )
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
fun SettingsSectionHeader(title: String, subTitle: String? = null) {
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

@Composable
fun ThemeOption(
  title: String,
  selected: Boolean,
  onClick: () -> Unit,
) {

  val themeText = stringResource(R.string.settings_theme_options_title)

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clickable(onClick = onClick)
      .fillMaxWidth()
      .padding(end = 16.dp)
      .clearAndSetSemantics {
        contentDescription = "$title $themeText"
        role = Role.RadioButton
        toggleableState = ToggleableState(selected)
      }
  ) {
    RadioButton(
      selected = selected,
      onClick = onClick,
      enabled = true
    )
    Text(
      text = title,
      style = AppTheme.typography.bodyCopySmall
    )
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
      isDarkTheme = isSystemInDarkTheme(),
      verName = "1.2.3",
      verCode = 123,
      selectSystemDefault = {},
      selectLight = {},
      selectDark = {},
      sendFeedback = {},
      copyInfo = {},
      navigateBack = {},
    )
  }
}

class SettingsScreenStateProvider : PreviewParameterProvider<Boolean?> {
  override val values: Sequence<Boolean?> = sequenceOf(null, false, true)
}
