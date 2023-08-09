package cm.aptoide.pt.feature_flags.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class SettingsLocalRepositoryImpl(
  private val featureFlagsDataStore: DataStore<Preferences>,
) : FeatureFlagsLocalRepository {

  companion object {
    private val FEATURE_FLAGS = stringSetPreferencesKey("featureFlags")
    private const val DELIMITER = "∰"
  }

  override suspend fun getFeatureFlags(): Map<String, String> =
    featureFlagsDataStore.data.map { it[FEATURE_FLAGS] }
      .firstOrNull()
      ?.map { it.split(DELIMITER) }
      ?.associate { it[0] to it[1] }
      ?: emptyMap()

  override suspend fun saveFeatureFlags(featureFlags: Map<String, String>) {
    featureFlagsDataStore.edit { preferences ->
      preferences[FEATURE_FLAGS] = featureFlags.entries
        .map { "${it.key}$DELIMITER${it.value}" }
        .toSet()
    }
  }
}
