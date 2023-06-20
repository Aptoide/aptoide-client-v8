package cm.aptoide.pt.di

import android.content.Context
import android.util.DisplayMetrics
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cm.aptoide.pt.BuildConfig
import cm.aptoide.pt.aptoide_network.data.network.UserAgentInterceptor
import cm.aptoide.pt.aptoide_network.di.StoreDomain
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.aptoide_network.di.VersionCode
import cm.aptoide.pt.feature_campaigns.data.CampaignUrlNormalizer
import cm.aptoide.pt.feature_home.di.WidgetsUrl
import cm.aptoide.pt.home.BottomNavigationManager
import cm.aptoide.pt.network.AptoideUserAgentInterceptor
import cm.aptoide.pt.profile.data.UserProfileRepository
import cm.aptoide.pt.profile.di.UserProfileDataStore
import cm.aptoide.pt.settings.data.DeviceInfoRepository
import cm.aptoide.pt.settings.data.UserPreferencesRepository
import cm.aptoide.pt.settings.di.UserPreferencesDataStore
import cm.aptoide.pt.userPreferencesDataStore
import cm.aptoide.pt.userProfileDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @Singleton
  @Provides
  fun provideBottomNavigationManager(): BottomNavigationManager = BottomNavigationManager()

  @Singleton
  @Provides
  @WidgetsUrl
  fun provideWidgetsUrl(): String = "getStoreWidgets?limit=25"

  @Singleton
  @Provides
  @StoreName
  fun provideStoreName(): String = BuildConfig.MARKET_NAME.lowercase(Locale.ROOT)

  @Singleton
  @Provides
  @StoreDomain
  fun provideEnvironmentDomain(): String = BuildConfig.STORE_DOMAIN

  @Singleton
  @Provides
  @VersionCode
  fun provideVersionCode(): Int = BuildConfig.VERSION_CODE

  @Provides
  @Singleton
  fun providesCampaignUrlNormalizer(@ApplicationContext context: Context): CampaignUrlNormalizer =
    CampaignUrlNormalizer(context)

  @Singleton
  @Provides
  @Named("aptoidePackage")
  fun provideAptoidePackage(): String {
    return BuildConfig.APPLICATION_ID
  }

  @Provides
  @Singleton
  fun providesUserAgentInterceptor(
    @Named("aptoidePackage") aptoidePackage: String,
    deviceInfoRepository: DeviceInfoRepository
  ): UserAgentInterceptor {
    return AptoideUserAgentInterceptor(
      idsRepository = null,
      architecture = System.getProperty("os.arch"),
      displayMetrics = DisplayMetrics(),
      versionName = BuildConfig.VERSION_NAME,
      aptoidePackage = aptoidePackage,
      aptoideMd5Manager = null,
      aptoideVersionCode = BuildConfig.VERSION_CODE,
      deviceInfoRepository = deviceInfoRepository
    )
  }

  @Singleton
  @Provides
  @UserProfileDataStore
  fun provideUserProfileDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
    return appContext.userProfileDataStore
  }

  @Singleton
  @Provides
  fun provideUserProfileRepository(
    @UserProfileDataStore dataStore: DataStore<Preferences>,
  ): UserProfileRepository {
    return UserProfileRepository(dataStore)
  }

  @Singleton
  @Provides
  @UserPreferencesDataStore
  fun provideUserPreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
    return appContext.userPreferencesDataStore
  }

  @Singleton
  @Provides
  fun provideUserPreferencesRepository(
    @UserPreferencesDataStore dataStore: DataStore<Preferences>,
  ): UserPreferencesRepository {
    return UserPreferencesRepository(dataStore)
  }
}
