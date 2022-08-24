package cm.aptoide.pt.feature_settings.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cm.aptoide.pt.theme.AppTheme
import cm.aptoide.pt.theme.AptoideTheme
import com.jamal.composeprefs.ui.GroupHeader
import com.jamal.composeprefs.ui.PrefsScreen
import com.jamal.composeprefs.ui.prefs.ListPref
import com.jamal.composeprefs.ui.prefs.SwitchPref
import com.jamal.composeprefs.ui.prefs.TextPref


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(dataStore: DataStore<Preferences>) {
  Column {
    PrefsScreen(dataStore = dataStore) {
      prefsGroup({
        GroupHeader(title = "General")
      }) {
        prefsItem {
          SwitchPref(
            key = "only_compatible",
            title = "Only show compatible apps",
            summary = "You'll only see apps compatible with this device."
          )
        }
        prefsItem {
          SwitchPref(
            key = "only_wifi",
            title = "Download only over wifi"
          )
        }
        prefsItem {
          SwitchPref(
            key = "show_beta",
            title = "Beta versions",
            summary = "You'll also see beta versions"
          )
        }
        prefsItem {
          ListPref(
            key = "theme",
            title = "Theme",
            summary = "Summary is the currently selected item",
            useSelectedAsSummary = true,
            entries = mapOf(
              "0" to "System Default",
              "1" to "Light",
              "2" to "Dark"
            )
          )
        }
        prefsItem {
          SwitchPref(
            key = "native_installer",
            title = "Use native installer",
            summary = "Only if you’re using MIUI (from Xiaomi) and are having problems to install apps. You may stop seeing some apps versions."
          )
        }
      }
      prefsGroup({
        GroupHeader(title = "Updates")
      }) {
        prefsItem {
          SwitchPref(
            key = "system_apps",
            title = "System Apps",
            summary = "You’ll see updates for system apps."
          )
        }
      }
      prefsGroup({
        GroupHeader(title = "Notifications")
      }) {
        prefsItem {
          SwitchPref(
            key = "campaigns",
            title = "Campaigns",
            summary = "Show notifications for app campaigns."
          )
        }
        prefsItem {
          SwitchPref(
            key = "updates",
            title = "App Updates",
            summary = "Show notifications for app updates."
          )
        }
        prefsItem {
          SwitchPref(
            key = "self_update",
            title = "Update Aptoide",
            summary = "Periodically check for new versions of Aptoide."
          )
        }
      }
      prefsGroup({
        GroupHeader(title = "Storage")
      }) {
        prefsItem {
          TextPref(title = "Clear cache", summary = "Delete temporary files.")
        }
        prefsItem {
          TextPref(title = "Set max. cache size (MB)",
            summary = "Set a maximum size for your cache")
        }
      }
      prefsGroup({
        GroupHeader(title = "Adult Content")
      }) {
        prefsItem {
          SwitchPref(key = "adult_content", title = "Show adult content")
        }
        prefsItem {
          TextPref(title = "Set adult content pin",
            summary = "Set a pin code to unlock adult content.")
        }
      }
      prefsGroup({
        GroupHeader(title = "Root")
      }) {
        prefsItem {
          SwitchPref(key = "root_install", title = "Allow root installation")
        }
        prefsItem {
          SwitchPref(key = "auto_updates", title = "Enable auto update",
            summary = "Enable automatic update of apps.")
        }
      }
      prefsGroup({
        GroupHeader(title = "Support")
      }) {
        prefsItem {
          TextPref(title = "Send feedback")
        }
        prefsItem {
          TextPref(title = "About us")
        }
        prefsItem {
          TextPref(title = "Hardware Specs")
        }
      }

      prefsGroup({
        GroupHeader(title = "Legal")
      }) {
        prefsItem {
          TextPref(title = "Terms and conditions")
        }
        prefsItem {
          TextPref(title = "Privacy Policy")
        }
      }
    }
  }
}

@Composable
fun AptoideActionBar() {
  AptoideTheme {
    TopAppBar(modifier = Modifier.padding(start = 16.dp, end = 16.dp),
      backgroundColor = AppTheme.colors.background, elevation = Dp(0f)
    ) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(modifier = Modifier.padding(start = 8.dp), text = "Aptoide")
        Icon(imageVector = Icons.Outlined.Settings,
          contentDescription = null)
      }
    }
  }
}
