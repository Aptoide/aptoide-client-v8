package cm.aptoide.pt.feature_apps.data.di

import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.feature_apps.data.*
import cm.aptoide.pt.feature_apps.data.network.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.feature_apps.data.network.firebase.AptoideFirebaseInfoProvider
import cm.aptoide.pt.feature_apps.data.network.service.AppsRemoteService
import cm.aptoide.pt.feature_apps.data.network.service.AptoideAppsNetworkService
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_campaigns.data.CampaignUrlNormalizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun providesAppsRepository(
    appsService: AppsRemoteService,
    campaignRepository: CampaignRepository,
    campaignUrlNormalizer: CampaignUrlNormalizer,
  ): AppsRepository {
    return AptoideAppsRepository(appsService, campaignRepository, campaignUrlNormalizer)
  }

  @Provides
  @Singleton
  fun providesAppsRemoteService(
    @RetrofitV7 retrofitV7: Retrofit,
    @StoreName storeName: String,
    analyticsInfoProvider: AptoideAnalyticsInfoProvider,
    messagingInfoProvider: AptoideFirebaseInfoProvider
  ): AppsRemoteService {
    return AptoideAppsNetworkService(
      appsRemoteDataSource = retrofitV7.create(AptoideAppsNetworkService.Retrofit::class.java),
      storeName = storeName,
      analyticsInfoProvider = analyticsInfoProvider,
      messagingInfoProvider = messagingInfoProvider
    )
  }
}
