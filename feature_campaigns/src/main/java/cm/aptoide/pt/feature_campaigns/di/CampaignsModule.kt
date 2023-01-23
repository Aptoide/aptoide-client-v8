package cm.aptoide.pt.feature_campaigns.di

import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_campaigns.data.CampaignApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CampaignsModule {

  @Provides
  @Singleton
  fun providesCampaignApiRepository(okHttpClient: OkHttpClient): CampaignRepository =
    CampaignApiRepository(okHttpClient)
}