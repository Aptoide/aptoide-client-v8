package cm.aptoide.pt.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

  // SETTERS
  // APP THEME
  suspend fun removeIsDarkTheme() {
    dataStore.edit { it.remove(DARK_THEME) }
  }

  suspend fun setIsDarkTheme(isDarkTheme: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[DARK_THEME] = isDarkTheme
    }
  }

  // GENERAL SECTION
  suspend fun setOnlyShowCompatibleApps(value: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[HWSPECS_FILTER] = value
    }
  }

  suspend fun setDownloadOnlyOverWifi(value: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[GENERAL_DOWNLOADS_WIFI_ONLY] = value
    }
  }

  suspend fun setBetaVersions(value: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[BETA_VERSIONS] = value
    }
  }

  suspend fun setUseNativeInstaller(value: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[USE_NATIVE_INSTALLER] = value
    }
  }

  // UPDATES SECTION
  suspend fun setSystemApps(value: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[SYSTEM_APPS] = value
    }
  }

  // NOTIFICATIONS SECTION
  suspend fun setCampaigns(value: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[CAMPAIGNS] = value
    }
  }

  suspend fun setAppUpdates(value: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[APP_UPDATES] = value
    }
  }

  suspend fun setUpdateAptoide(value: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[UPDATE_APTOIDE] = value
    }
  }

  // STORAGE SECTION
  suspend fun setMaxCacheSize(value: Int) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[MAX_CACHE_SIZE] = value
    }
  }

  // ADULT CONTENT SECTION
  suspend fun setUserPinCode(value: String) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[ADULT_CONTENT_PIN] = value
    }
  }

  suspend fun setShowAdultContent(value: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[SHOW_ADULT_CONTENT] = value
    }
  }

  // ROOT SECTION
  suspend fun setAllowRootInstallation(value: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[ALLOW_ROOT_INSTALLATION] = value
    }
  }

  suspend fun setEnableAutoUpdate(value: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[ENABLE_AUTO_UPDATE] = value
    }
  }

  // GETTERS
  // APP THEME
  fun isDarkTheme(): Flow<Boolean?> = dataStore.data.map { it[DARK_THEME] }

  // GENERAL SECTION
  fun isShowCompatibleApps(): Flow<Boolean?> = dataStore.data.map { it[HWSPECS_FILTER] }

  fun isDownloadOnlyOverWifi(): Flow<Boolean?> =
    dataStore.data.map { it[GENERAL_DOWNLOADS_WIFI_ONLY] }

  fun isBetaVersions(): Flow<Boolean?> = dataStore.data.map { it[BETA_VERSIONS] }

  fun isUseNativeInstaller(): Flow<Boolean?> = dataStore.data.map { it[USE_NATIVE_INSTALLER] }

  // UPDATES SECTION
  fun isSystemApps(): Flow<Boolean?> = dataStore.data.map { it[SYSTEM_APPS] }

  // NOTIFICATIONS SECTION
  fun isCampaigns(): Flow<Boolean?> = dataStore.data.map { it[CAMPAIGNS] }

  fun isAppUpdates(): Flow<Boolean?> = dataStore.data.map { it[APP_UPDATES] }

  fun isUpdateAptoide(): Flow<Boolean?> = dataStore.data.map { it[UPDATE_APTOIDE] }

  // STORAGE SECTION
  fun getMaxCacheSize(): Flow<Int> = dataStore.data.map { it[MAX_CACHE_SIZE] ?: 300 }

  // ADULT CONTENT SECTION
  fun getUserPinCode(): Flow<String> = dataStore.data.map { it[ADULT_CONTENT_PIN] ?: "" }

  fun isShowAdultContent(): Flow<Boolean?> = dataStore.data.map { it[SHOW_ADULT_CONTENT] }

  // ROOT SECTION
  fun isAllowRootInstallation(): Flow<Boolean?> = dataStore.data.map { it[ALLOW_ROOT_INSTALLATION] }

  fun isEnableAutoUpdate(): Flow<Boolean?> = dataStore.data.map { it[ENABLE_AUTO_UPDATE] }

  private companion object {
    val DARK_THEME = booleanPreferencesKey("dark_theme")

    // GENERAL SECTION
    val HWSPECS_FILTER = booleanPreferencesKey("hwspecsChkBox")
    val GENERAL_DOWNLOADS_WIFI_ONLY = booleanPreferencesKey("downloadwifionly")
    val BETA_VERSIONS = booleanPreferencesKey("updatesFilterAlphaBetaKey")
    val USE_NATIVE_INSTALLER = booleanPreferencesKey("forceNativeInstaller")

    // UPDATES SECTION
    val SYSTEM_APPS = booleanPreferencesKey("updatesSystemAppsKey")

    // NOTIFICATIONS SECTION
    val CAMPAIGNS = booleanPreferencesKey("notification_campaign_and_social")
    val APP_UPDATES = booleanPreferencesKey("notificationaptoide")
    val UPDATE_APTOIDE = booleanPreferencesKey("checkautoupdate")

    // STORAGE SECTION
    val MAX_CACHE_SIZE = intPreferencesKey("maxFileCache")

    // ADULT CONTENT SECTION
    val ADULT_CONTENT_PIN = stringPreferencesKey("Maturepin")
    val SHOW_ADULT_CONTENT = booleanPreferencesKey("matureChkBox")

    // ROOT SECTION
    val ALLOW_ROOT_INSTALLATION = booleanPreferencesKey("allowRoot")
    val ENABLE_AUTO_UPDATE = booleanPreferencesKey("auto_update")
  }
}
