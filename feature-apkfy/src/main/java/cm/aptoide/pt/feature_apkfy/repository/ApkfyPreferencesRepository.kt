package cm.aptoide.pt.feature_apkfy.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import cm.aptoide.pt.feature_apkfy.repository.ApkfyPreferencesRepository.PreferencesKeys.SHOULD_RUN_APKFY
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApkfyPreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

  private object PreferencesKeys {
    val SHOULD_RUN_APKFY = booleanPreferencesKey("SHOULD_RUN_APK_FY")
  }

  suspend fun shouldRunApkfy(): Boolean =
    dataStore.data.map { preferences ->
      preferences[SHOULD_RUN_APKFY] ?: true
    }.first()

  suspend fun setApkfyRan() {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[SHOULD_RUN_APKFY] = false
    }
  }
}
