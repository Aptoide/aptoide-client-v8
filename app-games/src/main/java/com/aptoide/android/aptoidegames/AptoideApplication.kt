package com.aptoide.android.aptoidegames

import android.app.Application
import android.content.Context
import android.os.Process
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Configuration
import androidx.work.Configuration.Provider
import cm.aptoide.pt.extensions.getProcessName
import cm.aptoide.pt.feature_campaigns.AptoideMMPCampaign
import cm.aptoide.pt.feature_campaigns.MMPLinkerCampaign
import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.feature_updates.domain.Updates
import cm.aptoide.pt.install_manager.InstallManager
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.appcoins.payments.di.Payments
import com.appcoins.payments.di.adyenKey
import com.appcoins.payments.di.adyenPaymentMethodFactory
import com.appcoins.payments.di.channel
import com.appcoins.payments.di.guestWalletUidPrefix
import com.appcoins.payments.di.paymentMethodFactories
import com.appcoins.payments.di.paymentScreenContentProvider
import com.appcoins.payments.di.paypalPaymentMethodFactory
import com.appcoins.payments.di.restClientInjectParams
import com.appcoins.payments.uri_handler.PaymentScreenContentProvider
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.feature_ad.Mintegral
import com.aptoide.android.aptoidegames.feature_companion_apps_notification.CompanionAppsManager
import com.aptoide.android.aptoidegames.feature_editors_choice_recommendation.EditorsChoiceRecommendationManager
import com.aptoide.android.aptoidegames.feature_payments.analytics.AGLogger
import com.aptoide.android.aptoidegames.home.repository.ThemePreferencesManager
import com.aptoide.android.aptoidegames.installer.analytics.ScheduledDownloadsListenerImpl
import com.aptoide.android.aptoidegames.installer.notifications.InstallerNotificationsManager
import com.aptoide.android.aptoidegames.launch.AppLaunchPreferencesManager
import com.aptoide.android.aptoidegames.network.AptoideGetHeaders
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "permissions")
val Context.userFeatureFlagsDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "userFeatureFlags"
)
val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "themePreferences"
)
val Context.networkPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "networkPreferences"
)
val Context.appLaunchDataStore: DataStore<Preferences> by preferencesDataStore(name = "appLaunch")
val Context.paymentsPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "paymentsPreferences"
)
val Context.idsDataStore: DataStore<Preferences> by preferencesDataStore(name = "ids")
val Context.subscribedAppsDataStore: DataStore<Preferences> by preferencesDataStore(name = "subscribedApps")
val Context.gamesFeedVisibilityDataStore: DataStore<Preferences> by preferencesDataStore(name = "gamesFeedVisibility")

@HiltAndroidApp
class AptoideApplication : Application(), ImageLoaderFactory, Provider {

  @EntryPoint
  @InstallIn(SingletonComponent::class)
  interface HiltWorkerFactoryEntryPoint {
    fun workerFactory(): HiltWorkerFactory
  }

  @Inject
  lateinit var featureFlags: FeatureFlags

  @Inject
  lateinit var installManager: InstallManager

  @Inject
  lateinit var installerNotificationsManager: InstallerNotificationsManager

  @Inject
  lateinit var scheduledDownloadsListenerImpl: ScheduledDownloadsListenerImpl

  @Inject
  lateinit var genericAnalytics: GenericAnalytics

  @Inject
  lateinit var themePreferencesManager: ThemePreferencesManager

  @Inject
  lateinit var agGetUserAgent: AptoideGetHeaders

  @Inject
  lateinit var agLogger: AGLogger

  @Inject
  lateinit var psContentProvider: PaymentScreenContentProvider

  @Inject
  lateinit var analyticsInfoProvider: AptoideAnalyticsInfoProvider

  @Inject
  lateinit var appLaunchPreferencesManager: AppLaunchPreferencesManager

  @Inject
  lateinit var biAnalytics: BIAnalytics

  @Inject
  lateinit var updates: Updates

  @Inject
  lateinit var mintegral: Mintegral

  @Inject
  lateinit var companionAppsManager: CompanionAppsManager

  @Inject
  lateinit var editorsChoiceRecommendationManager: EditorsChoiceRecommendationManager

  override fun onCreate() {
    FirebaseApp.initializeApp(this)
    super.onCreate()
    initTimber()
    initFeatureFlags()
    startInstallManager()
    initPayments()
    initIndicative()
    setUserProperties()
    AndroidViewModel(this).viewModelScope.launch {
      try {
        updates.initialize(this@AptoideApplication)
      } catch (e: Throwable) {
        Firebase.crashlytics.recordException(e)
      }
    }
    AptoideMMPCampaign.init(BuildConfig.OEMID, "aptoide")
    MMPLinkerCampaign.init(BuildConfig.OEMID)
    initAds()
    initCompanionAppsManager()
    initTrendingAppsRecommendationManager()
  }

  private fun initTrendingAppsRecommendationManager() {
    CoroutineScope(Dispatchers.IO).launch {
      editorsChoiceRecommendationManager.initialize()
    }
  }

  private fun initCompanionAppsManager() {
    CoroutineScope(Dispatchers.IO).launch {
      companionAppsManager.initialize()
    }
  }

  private fun initFeatureFlags() {
    CoroutineScope(Dispatchers.IO).launch {
      featureFlags.initialize()
    }
  }

  private fun initIndicative() {
    if (applicationContext.getProcessName(
        Process.myPid()
      ) != "${applicationContext.packageName}:obbMoverProcess"
    ) {
      biAnalytics.setup(
        context = applicationContext,
        installManager = installManager,
        analyticsInfoProvider = analyticsInfoProvider,
        appLaunchPreferencesManager = appLaunchPreferencesManager,
        featureFlags = featureFlags,
      )
    }
  }

  private fun initPayments() {
    Payments.init(
      application = this,
      environment = BuildConfig.PAYMENTS_ENVIRONMENT,
      logger = agLogger,
    ).apply {
      restClientInjectParams = agGetUserAgent
      guestWalletUidPrefix = "ag_"
      paymentMethodFactories = listOf(
        adyenPaymentMethodFactory,
        paypalPaymentMethodFactory
      )
      channel = "APTOIDEGAMES"
      paymentScreenContentProvider = psContentProvider
      adyenKey = BuildConfig.ADYEN_KEY
    }
  }

  private fun initAds() {
    if (applicationContext.getProcessName(
        Process.myPid()
      ) != "${applicationContext.packageName}:obbMoverProcess"
    ) {
      mintegral.initializeSdk()
    }
  }

  private fun setUserProperties() {
    genericAnalytics.setUserProperties(
      context = this,
      themePreferencesManager = themePreferencesManager,
      installManager = installManager,
    )
  }

  private fun startInstallManager() {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        installManager.restore()
      } catch (e: Exception) {
        Timber.e(e)
        e.printStackTrace()
      }

      installerNotificationsManager.initialize()
      scheduledDownloadsListenerImpl.initialize()
    }
  }

  override fun newImageLoader(): ImageLoader {
    return ImageLoader.Builder(this)
      .dispatcher(Dispatchers.Default)
      .allowHardware(true)
      .memoryCache {
        MemoryCache.Builder(this)
          // Set the max size to 25% of the app's available memory.
          .maxSizePercent(0.25)
          .build()
      }
      .diskCache {
        DiskCache.Builder()
          .directory(filesDir.resolve("image_cache"))
          .maxSizeBytes(512L * 1024 * 1024) // 512MB
          .build()
      }
      .okHttpClient {
        // Don't limit concurrent network requests by host.
        val dispatcher = Dispatcher().apply { maxRequestsPerHost = maxRequests }

        // Lazily create the OkHttpClient that is used for network operations.
        OkHttpClient.Builder()
          .dispatcher(dispatcher)
          .build()
      }
      // Enable logging to the standard Android log if this is a debug build.
      .apply { if (BuildConfig.DEBUG) logger(DebugLogger()) }
      .build()
  }

  private fun initTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(
        EntryPoints.get(this, HiltWorkerFactoryEntryPoint::class.java).workerFactory()
      ).build()
}
