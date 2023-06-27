package cm.aptoide.pt.network.repository

import android.content.Context
import android.os.Looper
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IdsRepository @Inject constructor(
  @ApplicationContext private val context: Context,
) {

  val aptoideClientUuid by lazy { getUniqueIdentifier() }

  private fun getUniqueIdentifier(): String {
    val id = getGoogleAdvertisingId()
    return if (!id.isNullOrEmpty())
      id else UUID.randomUUID().toString()
  }

  private fun getGoogleAdvertisingId(): String? =
    when {
      !isGooglePlayServicesAvailable() -> ""
      Looper.getMainLooper().thread === Thread.currentThread() ->
        throw IllegalStateException("You cannot run this method from the main thread")

      else -> try {
        AdvertisingIdClient.getAdvertisingIdInfo(context).id
      } catch (e: Exception) {
        throw IllegalStateException(e)
      }
    }

  private fun isGooglePlayServicesAvailable(): Boolean {
    return GoogleApiAvailability.getInstance()
      .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
  }
}
