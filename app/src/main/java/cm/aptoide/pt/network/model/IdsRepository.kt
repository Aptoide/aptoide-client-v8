package cm.aptoide.pt.network.model

import android.content.Context
import android.os.Looper
import androidx.annotation.WorkerThread
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IdsRepository @Inject constructor(
  private val context: Context,
  private val dataStore: DataStore<Preferences>,
) {

  @Synchronized fun getUniqueIdentifier(): rx.Single<String> {
    val aptoideId: String = getAndroidClientUUID()

    // if we already have the aptoide client uuid, return it
    if (aptoideId.isNotEmpty()) {
      Timber.tag(TAG).v("getUniqueIdentifier: in sharedPreferences: $aptoideId")
      return rx.Single.just(aptoideId)
    }

    return getGoogleAdvertisingId().map { id: String? ->
      if (!id.isNullOrEmpty())
        return@map id
      else
        return@map UUID.randomUUID().toString()
    }
      .doOnSuccess { id: String -> setAndroidClientUUID(id) }
  }

  @WorkerThread @Synchronized fun getGoogleAdvertisingId(): rx.Single<String> =
    rx.Single.just(getGoogleAdvertisingIDClient())
      .map<String> { id: String ->
        if (id.isNotEmpty()) {
          return@map id
        } else if (Looper.getMainLooper().thread === Thread.currentThread()) {
          throw IllegalStateException("You cannot run this method from the main thread")
        } else if (!AdNetworkUtils.isGooglePlayServicesAvailable(context)) {
          return@map ""
        } else {
          try {
            return@map AdvertisingIdClient.getAdvertisingIdInfo(context).id
          } catch (e: Exception) {
            throw IllegalStateException(e)
          }
        }
      }
      .doOnSuccess { id: String ->
        if (id.isNotEmpty()) {
          setGoogleAdvertisingIDClient(id)
        }
      }
      .subscribeOn(Schedulers.newThread())

  @Synchronized private fun getGoogleAdvertisingIDClient(): String {
    var aptoideId = ""
    runBlocking {
      aptoideId = dataStore.data.map { it[GOOGLE_ADVERTISING_ID_CLIENT] ?: "" }.first()
    }
    return aptoideId
  }

  @Synchronized private fun setGoogleAdvertisingIDClient(value: String) =
    runBlocking {
      dataStore.edit { it[GOOGLE_ADVERTISING_ID_CLIENT] = value }
    }

  @Synchronized private fun getAndroidClientUUID(): String {
    var aptoideId = ""
    runBlocking {
      aptoideId = dataStore.data.map { it[APTOIDE_CLIENT_UUID] ?: "" }.first()
    }
    return aptoideId
  }

  @Synchronized private fun setAndroidClientUUID(value: String) =
    runBlocking {
      dataStore.edit { it[APTOIDE_CLIENT_UUID] = value }
    }

  companion object {
    private val TAG = IdsRepository::class.java.simpleName
    private val APTOIDE_CLIENT_UUID = stringPreferencesKey("aptoide_client_uuid")
    private val GOOGLE_ADVERTISING_ID_CLIENT = stringPreferencesKey("googleAdvertisingId")
  }
}
