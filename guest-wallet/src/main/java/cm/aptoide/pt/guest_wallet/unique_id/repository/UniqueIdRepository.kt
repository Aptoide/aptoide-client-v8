package cm.aptoide.pt.guest_wallet.unique_id.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.aptoide.pt.feature_payment.di.OSPDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UniqueIdRepositoryImpl @Inject constructor(
  @OSPDataStore private val dataStore: DataStore<Preferences>
): UniqueIdRepository {

  private companion object {
    val UNIQUE_ID = stringPreferencesKey("unique_id")
  }

  override suspend fun getUniqueId(): String? =
    dataStore.data.map { preferences ->
      preferences[UNIQUE_ID]
    }.firstOrNull()

  override suspend fun storeUniqueId(id: String) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[UNIQUE_ID] = id
    }
  }
}
interface UniqueIdRepository {

  suspend fun getUniqueId(): String?
  suspend fun storeUniqueId(id: String)
}
