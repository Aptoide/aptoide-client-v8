package cm.aptoide.pt.feature_reactions.data.di

import cm.aptoide.pt.aptoide_network.di.RetrofitV8Echo
import cm.aptoide.pt.feature_reactions.AptoideReactionsRepository
import cm.aptoide.pt.feature_reactions.ReactionsNetworkService
import cm.aptoide.pt.feature_reactions.ReactionsRemoteService
import cm.aptoide.pt.feature_reactions.ReactionsRepository
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
  fun providesReactionsRepository(reactionsService: ReactionsRemoteService): ReactionsRepository {
    return AptoideReactionsRepository(reactionsService)
  }

  @Provides
  @Singleton
  fun providesReactionsRemoteService(
    @RetrofitV8Echo retrofitV8: Retrofit,
  ): ReactionsRemoteService {
    return ReactionsNetworkService(
      retrofitV8.create(ReactionsNetworkService.Retrofit::class.java))
  }
}