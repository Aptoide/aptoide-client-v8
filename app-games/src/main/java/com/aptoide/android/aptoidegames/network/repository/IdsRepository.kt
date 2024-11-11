package com.aptoide.android.aptoidegames.network.repository

import android.content.Context
import android.os.Looper
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IdsRepository @Inject constructor(
  @ApplicationContext private val context: Context,
) {

  val aptoideClientUuid by lazy { getUniqueIdentifier() }

  private fun getUniqueIdentifier(): String =
    getGoogleAdvertisingId()?.takeIf { it.isNotEmpty() } ?: UUID.randomUUID().toString()

  private fun getGoogleAdvertisingId(): String? =
    when {
      !isGooglePlayServicesAvailable() -> null
      Looper.getMainLooper().thread === Thread.currentThread() ->
        throw IllegalStateException("You cannot run this method from the main thread")

      else -> try {
        AdvertisingIdClient.getAdvertisingIdInfo(context).id
      } catch (e: Exception) {
        Timber.e(e)
        null
      }
    }

  private fun isGooglePlayServicesAvailable() = GoogleApiAvailability.getInstance()
    .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
}
