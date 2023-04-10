package cm.aptoide.pt.feature_categories.data.di

import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.feature_categories.data.network.service.CategoriesNetworkService
import cm.aptoide.pt.feature_categories.data.network.service.CategoriesRemoteService
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
  fun providesCategoriesRemoteService(
    @RetrofitV7 retrofitV7: Retrofit,
    @StoreName storeName: String,
  ): CategoriesRemoteService {
    return CategoriesNetworkService(
      categoriesRemoteDataSource = retrofitV7.create(CategoriesNetworkService.Retrofit::class.java),
      storeName = storeName
    )
  }
}
