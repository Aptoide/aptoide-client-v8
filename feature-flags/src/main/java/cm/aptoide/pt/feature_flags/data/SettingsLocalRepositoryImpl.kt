package cm.aptoide.pt.feature_flags.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class SettingsLocalRepositoryImpl(
  private val featureFlagsDataStore: DataStore<Preferences>,
) : FeatureFlagsLocalRepository {

  companion object {
    private val FEATURE_FLAGS = stringPreferencesKey("featureFlags")
  }

  override suspend fun getFeatureFlags(): JSONObject = try {
    featureFlagsDataStore.data
      .map { it[FEATURE_FLAGS] }
      .firstOrNull()
      .let { JSONObject(it ?: "{}") }
  } catch (t: Throwable) {
    JSONObject()
  }

  override suspend fun saveFeatureFlags(featureFlags: JSONObject) {
    featureFlagsDataStore.edit { preferences ->
      preferences[FEATURE_FLAGS] = featureFlags.toString()
    }
  }
}
