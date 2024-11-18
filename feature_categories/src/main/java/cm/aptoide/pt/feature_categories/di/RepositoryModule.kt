package cm.aptoide.pt.feature_categories.di

import cm.aptoide.pt.aptoide_network.di.RetrofitCategoriesApps
import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.feature_categories.analytics.AptoideFirebaseInfoProvider
import cm.aptoide.pt.feature_categories.data.AptoideCategoriesRepository
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
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
  fun providesCategoriesRepository(
    @RetrofitV7 retrofitV7: Retrofit,
    @RetrofitCategoriesApps retrofitCategoriesApps: Retrofit,
    @StoreName storeName: String,
    analyticsInfoProvider: AptoideAnalyticsInfoProvider,
    messagingInfoProvider: AptoideFirebaseInfoProvider
  ): CategoriesRepository {
    return AptoideCategoriesRepository(
      categoriesRemoteDataSourceGet = retrofitV7.create(AptoideCategoriesRepository.RetrofitGet::class.java),
      categoriesRemoteDataSourcePost = retrofitCategoriesApps.create(AptoideCategoriesRepository.RetrofitPost::class.java),
      storeName = storeName,
      analyticsInfoProvider = analyticsInfoProvider,
      messagingInfoProvider = messagingInfoProvider
    )
  }
}
