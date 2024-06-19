package com.aptoide.android.aptoidegames

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.install_manager.InstallManager
import com.appcoins.payments.di.Payments
import com.appcoins.payments.di.adyenEnvironment
import com.appcoins.payments.di.adyenKey
import com.appcoins.payments.di.adyenPaymentMethodFactory
import com.appcoins.payments.di.createGuestWalletProvider
import com.appcoins.payments.di.paymentMethodFactories
import com.appcoins.payments.di.paymentScreenContentProvider
import com.appcoins.payments.di.paypalPaymentMethodFactory
import com.appcoins.payments.di.restClientInjectParams
import com.appcoins.payments.di.walletProvider
import com.appcoins.payments.uri_handler.PaymentScreenContentProvider
import com.aptoide.android.aptoidegames.analytics.AGLogger
import com.aptoide.android.aptoidegames.installer.notifications.InstallerNotificationsManager
import com.aptoide.android.aptoidegames.network.AptoideGetHeaders
import com.indicative.client.android.Indicative
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
val Context.paymentsPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "paymentsPreferences")

@HiltAndroidApp
class AptoideApplication : Application() {

  @Inject
  lateinit var installManager: InstallManager

  @Inject
  lateinit var installerNotificationsManager: InstallerNotificationsManager

  @Inject
  lateinit var agGetUserAgent: AptoideGetHeaders

  @Inject
  lateinit var agLogger: AGLogger

  @Inject
  lateinit var psContentProvider: PaymentScreenContentProvider

  @Inject
  lateinit var analyticsInfoProvider: AptoideAnalyticsInfoProvider

  override fun onCreate() {
    super.onCreate()
    initTimber()
    startInstallManager()
    initPayments()
    initIndicative()
  }

  private fun initIndicative() {
    CoroutineScope(Dispatchers.Main).launch {
      Indicative.launch(applicationContext, BuildConfig.INDICATIVE_KEY)
      Indicative.setUniqueID(analyticsInfoProvider.getAnalyticsId())
    }
  }

  private fun initPayments() {
    Payments.init(
      application = this,
      environment = BuildConfig.PAYMENTS_ENVIRONMENT,
      logger = agLogger,
    ).apply {
      restClientInjectParams = agGetUserAgent
      walletProvider = createGuestWalletProvider("ag_")
      paymentMethodFactories = listOf(
        adyenPaymentMethodFactory,
        paypalPaymentMethodFactory
      )
      paymentScreenContentProvider = psContentProvider
      adyenKey = BuildConfig.ADYEN_KEY
      adyenEnvironment = BuildConfig.ADYEN_ENVIRONMENT
    }
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
    }
  }

  private fun initTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }
}
