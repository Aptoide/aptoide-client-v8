package com.aptoide.android.aptoidegames.device_info

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.aptoide.pt.environment_info.PaEDeviceIdentity
import kotlinx.coroutines.flow.first
import java.security.MessageDigest
import java.util.UUID

/** Reads `Settings.Secure.ANDROID_ID`; behind an interface so the logic is JVM-testable. */
fun interface AndroidIdSource {
  fun getAndroidId(): String?
}

class SettingsAndroidIdSource(private val context: Context) : AndroidIdSource {
  // Hashed before it leaves the device; see AGPaEDeviceIdentity.
  @SuppressLint("HardwareIds")
  override fun getAndroidId(): String? =
    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}

/** Keeps the install id. [getOrCreate] must create it at most once, even under concurrent calls. */
interface InstallIdStore {
  suspend fun getOrCreate(newId: () -> String): String
}

class DataStoreInstallIdStore(private val dataStore: DataStore<Preferences>) : InstallIdStore {

  // DataStore runs edits one at a time, each on the latest data, so two first calls racing each
  // other can't both create an id: the second one sees the id the first one wrote.
  override suspend fun getOrCreate(newId: () -> String): String {
    read()?.let { return it }
    return dataStore.edit { preferences ->
      if (preferences[INSTALL_ID].isNullOrBlank()) preferences[INSTALL_ID] = newId()
    }[INSTALL_ID]!!
  }

  suspend fun read(): String? = dataStore.data.first()[INSTALL_ID]?.takeIf { it.isNotBlank() }

  private companion object {
    val INSTALL_ID = stringPreferencesKey("pae_install_id")
  }
}

/**
 * Both values are constant for the life of the process, so they are worked out once and cached:
 * the interceptor asks for them on every Play & Earn request.
 */
class AGPaEDeviceIdentity(
  private val androidIdSource: AndroidIdSource,
  private val installIdStore: InstallIdStore,
  private val newInstallId: () -> String = { UUID.randomUUID().toString() },
) : PaEDeviceIdentity {

  private val deviceKey: String? by lazy {
    runCatching { androidIdSource.getAndroidId() }
      .getOrNull()
      ?.takeIf { it.isNotBlank() }
      ?.let(::sha256Hex)
  }

  @Volatile
  private var installId: String? = null

  override suspend fun getDeviceKey(): String? = deviceKey

  override suspend fun getInstallId(): String =
    installId ?: installIdStore.getOrCreate(newInstallId).also { installId = it }
}

/** Lower-case hex SHA-256 of the UTF-8 bytes of [value]. */
internal fun sha256Hex(value: String): String =
  MessageDigest.getInstance("SHA-256")
    .digest(value.toByteArray(Charsets.UTF_8))
    .joinToString(separator = "") { "%02x".format(it) }
