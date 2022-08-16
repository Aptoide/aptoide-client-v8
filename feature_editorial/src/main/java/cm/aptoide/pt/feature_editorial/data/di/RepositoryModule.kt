package cm.aptoide.pt.feature_editorial.data.di

import cm.aptoide.pt.aptoide_network.di.RetrofitV7ActionItem
import cm.aptoide.pt.feature_editorial.data.AptoideEditorialRepository
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.data.network.EditorialRemoteService
import cm.aptoide.pt.feature_editorial.data.network.service.EditorialNetworkService
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
  fun providesEditorialRepository(editorialRemoteService: EditorialRemoteService): EditorialRepository {
    return AptoideEditorialRepository(editorialRemoteService)
  }

  @Provides
  @Singleton
  fun provideEditorialRemoteService(
    @RetrofitV7ActionItem retrofit: Retrofit,
  ): EditorialRemoteService {
    return EditorialNetworkService(retrofit.create(EditorialNetworkService.Retrofit::class.java))
  }
}