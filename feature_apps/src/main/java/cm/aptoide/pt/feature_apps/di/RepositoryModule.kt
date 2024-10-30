package cm.aptoide.pt.feature_apps.di

import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.feature_apps.data.AppRepository
import cm.aptoide.pt.feature_apps.data.AppsListRepository
import cm.aptoide.pt.feature_apps.data.AptoideAppRepository
import cm.aptoide.pt.feature_apps.data.AptoideAppsListRepository
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun providesAppRepository(
    @RetrofitV7 retrofitV7: Retrofit,
    @StoreName storeName: String,
    campaignRepository: CampaignRepository,
  ): AppRepository = AptoideAppRepository(
    appsRemoteDataSource = retrofitV7.create(AptoideAppRepository.Retrofit::class.java),
    storeName = storeName,
    campaignRepository = campaignRepository,
    scope = CoroutineScope(Dispatchers.IO)
  )

  @Provides
  @Singleton
  fun providesAppsRepository(
    @RetrofitV7 retrofitV7: Retrofit,
    @StoreName storeName: String,
    campaignRepository: CampaignRepository,
  ): AppsListRepository = AptoideAppsListRepository(
    appsRemoteDataSource = retrofitV7.create(AptoideAppsListRepository.Retrofit::class.java),
    storeName = storeName,
    campaignRepository = campaignRepository,
    scope = CoroutineScope(Dispatchers.IO)
  )
}
