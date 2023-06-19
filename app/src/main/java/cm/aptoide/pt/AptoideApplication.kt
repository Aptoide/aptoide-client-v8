package cm.aptoide.pt

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import cm.aptoide.pt.analytics.Analytics
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.network.model.AptoideMd5Manager
import cm.aptoide.pt.network.model.IdsRepository
import cm.aptoide.pt.settings.data.UserPreferencesRepository
import dagger.hilt.android.HiltAndroidApp
import io.rakam.api.Rakam
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import rx.Completable
import timber.log.Timber
import javax.inject.Inject

val Context.securityDataStore: DataStore<Preferences> by preferencesDataStore(name = "securityDataStore")
val Context.md5DataStore: DataStore<Preferences> by preferencesDataStore(name = "md5DataStore")
val Context.userProfileDataStore: DataStore<Preferences> by preferencesDataStore(name = "userProfile")
val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "userPreferences",
  produceMigrations = { context ->
    listOf(SharedPreferencesMigration({ PreferenceManager.getDefaultSharedPreferences(context) }))
  }
)

@HiltAndroidApp
class AptoideApplication : Application() {

  @Inject
  lateinit var installManager: InstallManager

  @Inject
  lateinit var analytics: Analytics

  @Inject
  @StoreName
  lateinit var storeName: String

  @Inject
  lateinit var userPreferencesRepository: UserPreferencesRepository

  @Inject
  lateinit var idsRepository: IdsRepository

  @Inject
  lateinit var aptoideMd5Manager: AptoideMd5Manager

  override fun onCreate() {
    super.onCreate()
    initTimber()
    startInstallManager()
    setUserProperties()
    setUpAptoideUuid()
    calculateMd5Sum()
  }

  private fun setUserProperties() {
    analytics.setUserProperties(storeName = storeName)

    CoroutineScope(Dispatchers.IO).launch {
      val isDarkTheme = userPreferencesRepository.isDarkTheme().first()
        ?: (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)

      analytics.setUserProperties(isDarkTheme = isDarkTheme)
    }
  }

  private fun startInstallManager() {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        installManager.restore()
      } catch (e: Exception) {
        e.printStackTrace()
        Timber.e(e)
      }
    }
  }

  private fun initTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }

  private fun calculateMd5Sum() {
    aptoideMd5Manager.calculateMd5Sum()
  }

  private fun setUpAptoideUuid() {
    generateAptoideUuid().andThen(
      Completable.mergeDelayError(setUpInitialAdsUserProperty()),
    )
  }

  private fun generateAptoideUuid(): Completable {
    return Completable.fromAction { idsRepository.getUniqueIdentifier() }
  }

  private fun setUpInitialAdsUserProperty(): Completable {
    return idsRepository.getUniqueIdentifier()
      .flatMapCompletable { Completable.complete() }
      .doOnCompleted {
        Rakam.getInstance()
          .enableForegroundTracking(this)
      }
  }
}
