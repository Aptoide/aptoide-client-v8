package cm.aptoide.pt.di

import android.content.Context
import cm.aptoide.pt.BuildConfig
import cm.aptoide.pt.apps.WidgetsNetworkService
import cm.aptoide.pt.aptoide_network.data.network.UserAgentInterceptor
import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.aptoide_network.di.StoreDomain
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.aptoide_network.di.VersionCode
import cm.aptoide.pt.feature_campaigns.data.CampaignUrlNormalizer
import cm.aptoide.pt.feature_home.data.network.service.WidgetsRemoteService
import cm.aptoide.pt.home.BottomNavigationManager
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.network.AptoideUserAgentInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @Singleton
  @Provides
  fun provideBottomNavigationManager(): BottomNavigationManager = BottomNavigationManager()

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
  fun providesWidgetsRemoteService(
    @RetrofitV7 retrofitV7: Retrofit,
    @StoreName storeName: String
  ): WidgetsRemoteService = WidgetsNetworkService(
    widgetsRemoteDataSource = retrofitV7.create(WidgetsNetworkService.Retrofit::class.java),
    storeName = storeName
  )

  @Singleton
  @Provides
  fun provideInstallManager(@ApplicationContext appContext: Context): InstallManager =
    InstallManager.Builder(appContext).build()

  @Provides
  @Singleton
  fun providesCampaignUrlNormalizer(@ApplicationContext context: Context): CampaignUrlNormalizer =
    CampaignUrlNormalizer(context)

  @Provides
  @Singleton
  fun providesUserAgentInterceptor(): UserAgentInterceptor {
    return AptoideUserAgentInterceptor()
  }
}
