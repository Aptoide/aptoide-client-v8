package cm.aptoide.pt

import android.content.SharedPreferences
import cm.aptoide.pt.preferences.secure.SecurePreferences
import rx.Completable

/**
 * Class responsible for first launch and update launches.
 */

class LaunchManager(private val firstLaunchManager: FirstLaunchManager,
                    private val updateLaunchManager: UpdateLaunchManager,
                    private val secureSharedPreferences: SharedPreferences) {

  fun launch(): Completable {
    return Completable.mergeDelayError(runFirstLaunch(), runUpdateLaunch(), updateLaunchSettings())
  }

  /**
   * Checks if it is the first ever launch of the app and calls FirstLaunchManager
   */
  private fun runFirstLaunch(): Completable {
    if (SecurePreferences.isFirstRun(secureSharedPreferences)) {
      return firstLaunchManager.runFirstLaunch()
    }
    return Completable.complete()
  }

  /**
   * Checks if it is the first launch SINCE an update and calls UpdateLaunchManager
   */
  private fun runUpdateLaunch(): Completable {
    val isFirstRun = SecurePreferences.isFirstRun(secureSharedPreferences)
    val currentVersion = BuildConfig.VERSION_CODE
    val latestRecordedVersion = SecurePreferences.getLatestVersionCode(secureSharedPreferences)
    if (!isFirstRun && currentVersion > latestRecordedVersion) {
      return updateLaunchManager.runUpdateLaunch(latestRecordedVersion, currentVersion)
    }
    return Completable.complete();
  }

  /**
   * Updates the internal values of first launch and current version
   */
  private fun updateLaunchSettings(): Completable {
    return Completable.fromAction {
      SecurePreferences.setFirstRun(false, secureSharedPreferences)
      SecurePreferences.setCurrentVersionCode(BuildConfig.VERSION_CODE, secureSharedPreferences)
    }
  }

}