package cm.aptoide.pt.home

import android.content.SharedPreferences
import rx.Observable

class GDPRDialogManager(private val sharedPreferences: SharedPreferences) {

  private var GDPR_PREFERENCE_KEY: String = "ACCEPTED_GDPR"

  fun saveAcceptedGDPR() {
    sharedPreferences.edit().putBoolean(GDPR_PREFERENCE_KEY, true).apply()
  }

  fun hasAcceptedGDPR(): Observable<Boolean> {
    return Observable.just(sharedPreferences.getBoolean(GDPR_PREFERENCE_KEY, false))
  }
}