package cm.aptoide.pt.feature_apps.data.di

import cm.aptoide.pt.feature_apps.data.AptoideWidgetsRepository
import cm.aptoide.pt.feature_apps.data.WidgetsRepository
import cm.aptoide.pt.feature_apps.data.network.service.WidgetsRemoteService
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
  fun providesWidgetsRepository(retrofitV7: Retrofit): WidgetsRepository {
    return AptoideWidgetsRepository(retrofitV7.create(WidgetsRemoteService::class.java))
  }
}