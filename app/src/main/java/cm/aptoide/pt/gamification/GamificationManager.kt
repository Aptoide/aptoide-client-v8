package cm.aptoide.pt.gamification

import android.content.SharedPreferences
import cm.aptoide.pt.abtesting.experiments.ApkfyGamificationExperiment
import cm.aptoide.pt.install.InstallManager
import rx.Observable
import rx.Single
import java.text.DecimalFormat

class GamificationManager(private val apkfyGamificationExperiment: ApkfyGamificationExperiment,
                          private val sharedPreferences: SharedPreferences,
                          private val installManager: InstallManager) {

  companion object {
    private var GAMIFICATION_INSTALL_TIME = "GAMIFICATION_INSTALL_TIME"
    private var TWENTY_FOUR_HOURS = 2 * 60 * 1000//24 * 60 * 60 * 1000
    private var WALLET_PACKAGE = "com.appcoins.wallet"
  }

  fun shouldShowApkfyGamification(): Single<Boolean> {
    //return apkfyGamificationExperiment.shouldShowGamification()
    return Single.just(true)
  }

  fun shouldShowHomeGamification(): Single<Boolean> {
    //TODO: Add home experiment
    return Single.just(true)
  }

  fun isSecondLevelComplete(): Single<Boolean> {
    return installManager.isInstalled(WALLET_PACKAGE).toSingle()
  }

  fun setUpSecondChallenge(): Observable<Int> {
    var counter = -1
    return installManager.installedApps
        .map { counter++ }
        .filter { counter == 1 }
        .doOnNext {
          sharedPreferences.edit().putLong(GAMIFICATION_INSTALL_TIME, System.currentTimeMillis())
              .apply()
          //setUpNotification()
        }
  }

  private fun setUpNotification() {
    TODO("Not yet implemented")
  }

  fun isFirstLevelComplete(): Single<Boolean> {
    val installTime = sharedPreferences.getLong(GAMIFICATION_INSTALL_TIME, -1)
    return Single.just(installTime > 0)
  }

  fun isSecondChallengeUnlocked(): Boolean {
    val installTime = sharedPreferences.getLong(GAMIFICATION_INSTALL_TIME, -1)
    return System.currentTimeMillis() >= installTime + TWENTY_FOUR_HOURS
  }

  fun getSecondChallengeTimeLeft(): String {
    val result = (sharedPreferences.getLong(GAMIFICATION_INSTALL_TIME,
        -1) + TWENTY_FOUR_HOURS - System.currentTimeMillis()) * 1000 * 60 * 60
    if (result >= 1)
      return DecimalFormat("#").format(result).toString() + " hours"
    else
      return DecimalFormat("##").format(result / 60).toString() + " minutes"
  }
}