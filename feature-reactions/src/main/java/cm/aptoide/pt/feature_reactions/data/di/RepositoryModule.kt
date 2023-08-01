package cm.aptoide.pt.feature_reactions.data.di

import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import cm.aptoide.pt.feature_reactions.AptoideReactionsRepository
import cm.aptoide.pt.feature_reactions.ReactionsNetworkService
import cm.aptoide.pt.feature_reactions.ReactionsRemoteService
import cm.aptoide.pt.feature_reactions.ReactionsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @RetrofitReactions
  @Provides
  @Singleton
  fun provideRetrofitReactions(@BaseOkHttp okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl("https://reactions.api.aptoide.com/echo/8.20181122/")
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @Provides
  @Singleton
  fun providesReactionsRepository(reactionsService: ReactionsRemoteService): ReactionsRepository {
    return AptoideReactionsRepository(reactionsService)
  }

  @Provides
  @Singleton
  fun providesReactionsRemoteService(
    @RetrofitReactions retrofitReactions: Retrofit,
  ): ReactionsRemoteService {
    return ReactionsNetworkService(
      retrofitReactions.create(ReactionsNetworkService.Retrofit::class.java)
    )
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitReactions
