package cm.aptoide.pt.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Analytics @Inject constructor(
  private val firebaseAnalytics: FirebaseAnalytics,
) {

  fun setUserProperties(
    storeName: String? = null,
    isDarkTheme: Boolean? = null,
  ) {
    storeName?.also {
      firebaseAnalytics.setUserProperty("store_name", storeName)
    }
    isDarkTheme?.also {
      firebaseAnalytics.setUserProperty(
        "theme",
        if (it) "system_dark" else "system_light"
      )
    }
  }
}
