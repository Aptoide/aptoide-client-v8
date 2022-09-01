package cm.aptoide.pt.feature_settings.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cm.aptoide.pt.feature_settings.R
import com.jamal.composeprefs.ui.GroupHeader
import com.jamal.composeprefs.ui.PrefsScreen
import com.jamal.composeprefs.ui.prefs.ListPref
import com.jamal.composeprefs.ui.prefs.SwitchPref
import com.jamal.composeprefs.ui.prefs.TextPref


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(dataStore: DataStore<Preferences>) {
  var localContext = LocalContext.current
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
          Row(modifier = Modifier.clickable {
            openTab(localContext,
              "https://en.aptoide.com/company/legal?section=terms")
          }) {
            TextPref(title = "Terms and conditions")
          }
        }
        prefsItem {
          TextPref(title = "Privacy Policy")
        }
      }
    }
  }
}

fun openTab(context: Context, url: String) {
  val packageName = "com.android.chrome"

  val builder = CustomTabsIntent.Builder()
  builder.setShowTitle(true)
  builder.setInstantAppsEnabled(true)
  builder.setToolbarColor(ContextCompat.getColor(context, R.color.cardview_shadow_end_color))
  val customBuilder = builder.build()

  customBuilder.intent.setPackage(packageName)
  customBuilder.launchUrl(context, Uri.parse(url))

}