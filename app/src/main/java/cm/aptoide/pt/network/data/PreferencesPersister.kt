package cm.aptoide.pt.network.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesPersister @Inject constructor(private val datastore: DataStore<Preferences>) {

  suspend fun set(value: String) {
    datastore.edit { it[APTOIDE_MD5] = value }
  }

  fun get(): Flow<String> = datastore.data.map { it[APTOIDE_MD5] ?: "" }

  companion object {
    val APTOIDE_MD5 = stringPreferencesKey("aptoide_md5")
  }
}
