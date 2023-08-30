package cm.aptoide.pt.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cm.aptoide.pt.BuildConfig
import cm.aptoide.pt.aptoide_network.data.network.QLogicInterceptor
import cm.aptoide.pt.aptoide_network.data.network.UserAgentInterceptor
import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import cm.aptoide.pt.aptoide_network.di.RetrofitBuzz
import cm.aptoide.pt.aptoide_network.di.StoreDomain
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.aptoide_network.di.VersionCode
import cm.aptoide.pt.environment_info.DeviceInfo
import cm.aptoide.pt.feature_campaigns.data.CampaignUrlNormalizer
import cm.aptoide.pt.feature_flags.AptoideFeatureFlagsRepository
import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import cm.aptoide.pt.feature_flags.di.FeatureFlagsDataStore
import cm.aptoide.pt.feature_home.di.WidgetsUrl
import cm.aptoide.pt.feature_search.data.AutoCompleteSuggestionsRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchStoreManager
import cm.aptoide.pt.network.AptoideQLogicInterceptor
import cm.aptoide.pt.network.AptoideUserAgentInterceptor
import cm.aptoide.pt.network.repository.IdsRepository
import cm.aptoide.pt.profile.data.UserProfileRepository
import cm.aptoide.pt.profile.di.UserProfileDataStore
import cm.aptoide.pt.search.repository.AptoideAutoCompleteSuggestionsRepository
import cm.aptoide.pt.search.repository.AptoideSearchStoreManager
import cm.aptoide.pt.search.repository.AptoideAutoCompleteSuggestionsRepository.AutoCompleteSearchRetrofitService
import cm.aptoide.pt.settings.di.UserPreferencesDataStore
import cm.aptoide.pt.settings.repository.UserPreferencesRepository
import cm.aptoide.pt.userFeatureFlagsDataStore
import cm.aptoide.pt.userPreferencesDataStore
import cm.aptoide.pt.userProfileDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
  fun providesIdsRepository(
    @ApplicationContext context: Context,
  ): IdsRepository {
    return IdsRepository(
      context = context,
    )
  }

  @Provides
  @Singleton
  fun providesUserAgentInterceptor(
    packageManager: PackageManager,
    idsRepository: IdsRepository,
    deviceInfo: DeviceInfo
  ): UserAgentInterceptor {
    return AptoideUserAgentInterceptor(
      packageManager = packageManager,
      idsRepository = idsRepository,
      deviceInfo = deviceInfo,
    )
  }

  @Provides
  @Singleton
  fun providesQLogicInterceptor(
    userPreferencesRepository: UserPreferencesRepository,
    deviceInfo: DeviceInfo
  ): QLogicInterceptor {
    return AptoideQLogicInterceptor(
      userPreferencesRepository = userPreferencesRepository,
      deviceInfo = deviceInfo,
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

  @Singleton
  @Provides
  fun provideSearchStoreManager(): SearchStoreManager = AptoideSearchStoreManager()

  @RetrofitBuzz
  @Provides
  @Singleton
  fun provideSearchAutoCompleteRetrofit(@BaseOkHttp okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(BuildConfig.SEARCH_BUZZ_DOMAIN)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @Singleton
  @Provides
  fun provideAutoCompleteSuggestionsRepository(
    @RetrofitBuzz retrofitBuzz: Retrofit,
  ): AutoCompleteSuggestionsRepository {
    return AptoideAutoCompleteSuggestionsRepository(
      retrofitBuzz.create(AutoCompleteSearchRetrofitService::class.java),
    )
  }

  @Singleton
  @Provides
  @FeatureFlagsDataStore
  fun provideUserFeatureFlagsDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> =
    appContext.userFeatureFlagsDataStore

  @Provides
  @Singleton
  fun provideAptoideFeatureFlagsRepository(): FeatureFlagsRepository =
    AptoideFeatureFlagsRepository()
}
