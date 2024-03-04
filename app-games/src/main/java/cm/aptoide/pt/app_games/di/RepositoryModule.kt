package cm.aptoide.pt.app_games.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cm.aptoide.pt.app_games.BuildConfig
import cm.aptoide.pt.app_games.network.AptoideGetUserAgent
import cm.aptoide.pt.app_games.network.AptoideQLogicInterceptor
import cm.aptoide.pt.app_games.userPreferencesDataStore
import cm.aptoide.pt.aptoide_network.data.network.GetUserAgent
import cm.aptoide.pt.aptoide_network.data.network.QLogicInterceptor
import cm.aptoide.pt.aptoide_network.di.StoreDomain
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.aptoide_network.di.VersionCode
import cm.aptoide.pt.environment_info.DeviceInfo
import cm.aptoide.pt.feature_campaigns.data.CampaignUrlNormalizer
import cm.aptoide.pt.feature_home.di.WidgetsUrl
import cm.aptoide.pt.settings.di.UserPreferencesDataStore
import cm.aptoide.pt.settings.repository.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

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

  @Provides
  @Singleton
  fun providesUserAgentInterceptor(aptoideGetUserAgent: AptoideGetUserAgent): GetUserAgent =
    aptoideGetUserAgent

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

  @Singleton
  @Provides
  @VersionCode
  fun provideVersionCode(): Int = BuildConfig.VERSION_CODE


  @Provides
  @Singleton
  fun providesCampaignUrlNormalizer(@ApplicationContext context: Context): CampaignUrlNormalizer =
    CampaignUrlNormalizer(context)

  @Provides
  @Singleton
  fun providesQLogicInterceptor(
    userPreferencesRepository: UserPreferencesRepository,
    deviceInfo: DeviceInfo,
  ): QLogicInterceptor {
    return AptoideQLogicInterceptor(
      userPreferencesRepository = userPreferencesRepository,
      deviceInfo = deviceInfo,
    )
  }
}
