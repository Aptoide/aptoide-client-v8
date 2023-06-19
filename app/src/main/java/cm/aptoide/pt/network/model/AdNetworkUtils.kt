package cm.aptoide.pt.network.model

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

object AdNetworkUtils {
  fun isGooglePlayServicesAvailable(context: Context): Boolean {
    return GoogleApiAvailability.getInstance()
      .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
  }
}
