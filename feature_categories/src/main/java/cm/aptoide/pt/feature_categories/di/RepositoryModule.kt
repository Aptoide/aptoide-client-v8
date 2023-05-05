package cm.aptoide.pt.feature_categories.di

import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.feature_categories.analytics.AptoideFirebaseInfoProvider
import cm.aptoide.pt.feature_categories.data.AptoideCategoriesRepository
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import cm.aptoide.pt.feature_home.data.WidgetsRepository
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
    @StoreName storeName: String,
    widgetsRepository: WidgetsRepository,
    analyticsInfoProvider: AptoideAnalyticsInfoProvider,
    messagingInfoProvider: AptoideFirebaseInfoProvider
  ): CategoriesRepository {
    return AptoideCategoriesRepository(
      widgetsRepository = widgetsRepository,
      categoriesRemoteDataSource = retrofitV7.create(AptoideCategoriesRepository.Retrofit::class.java),
      storeName = storeName,
      analyticsInfoProvider = analyticsInfoProvider,
      messagingInfoProvider = messagingInfoProvider
    )
  }
}
