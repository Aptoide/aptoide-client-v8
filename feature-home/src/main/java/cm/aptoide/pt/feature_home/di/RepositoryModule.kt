package cm.aptoide.pt.feature_home.di

import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.feature_home.data.AptoideWidgetsRepository
import cm.aptoide.pt.feature_home.data.WidgetsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun providesWidgetsRepository(
    @RetrofitV7 retrofitV7: Retrofit,
    @StoreName storeName: String,
    @WidgetsUrl widgetsUrl: String,
  ): WidgetsRepository = AptoideWidgetsRepository(
    widgetsRemoteDataSource = retrofitV7.create(AptoideWidgetsRepository.Retrofit::class.java),
    storeName = storeName,
    widgetsUrl = widgetsUrl,
    scope = CoroutineScope(Dispatchers.IO)
  )
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WidgetsUrl
