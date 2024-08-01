package cm.aptoide.pt.feature_editorial.di

import cm.aptoide.pt.aptoide_network.di.RetrofitV7ActionItem
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_editorial.data.AptoideEditorialRepository
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun providesEditorialRepository(
    campaignRepository: CampaignRepository,
    @RetrofitV7ActionItem retrofit: Retrofit,
    @StoreName storeName: String,
  ): EditorialRepository = AptoideEditorialRepository(
    campaignRepository,
    retrofit.create(AptoideEditorialRepository.Retrofit::class.java),
    storeName
  )
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultEditorialUrl
