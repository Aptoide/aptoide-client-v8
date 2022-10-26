package cm.aptoide.pt.feature_campaigns.di

import cm.aptoide.pt.aptoide_network.di.RetrofitAptWords
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_campaigns.CampaignsRepository
import cm.aptoide.pt.feature_campaigns.data.CampaignApiRepository
import cm.aptoide.pt.feature_campaigns.data.CampaignsApi
import cm.aptoide.pt.feature_campaigns.data.CampaignsApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CampaignsModule {

  @Provides
  @Singleton
  fun providesCampaignsApi(@RetrofitAptWords retrofitAptWords: Retrofit): CampaignsApi =
    retrofitAptWords.create(CampaignsApi::class.java)

  @Provides
  @Singleton
  fun providesCampaignApiRepository(okHttpClient: OkHttpClient): CampaignRepository =
    CampaignApiRepository(okHttpClient)

  @Provides
  @Singleton
  fun providesCampaignsApiRepository(
    campaignsApi: CampaignsApi,
    campaignRepository: CampaignRepository
  ): CampaignsRepository =
    CampaignsApiRepository(campaignsApi, campaignRepository)
}