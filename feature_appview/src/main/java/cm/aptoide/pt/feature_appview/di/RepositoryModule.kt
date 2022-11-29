package cm.aptoide.pt.feature_appview.di

import cm.aptoide.pt.aptoide_network.di.RetrofitV7ActionItem
import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_appview.data.AptoideAppViewRepository
import cm.aptoide.pt.feature_appview.data.network.RemoteAppViewRepository
import cm.aptoide.pt.feature_appview.data.network.service.AppViewNetworkService
import cm.aptoide.pt.feature_appview.domain.repository.AppViewRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

  @Singleton
  @Provides
  fun provideAppViewRepository(
    appsRepository: AppsRepository,
    remoteAppViewRepository: RemoteAppViewRepository,
  ): AppViewRepository {
    return AptoideAppViewRepository(appsRepository, remoteAppViewRepository)
  }

  @Singleton
  @Provides
  fun provideRemoteAppViewRepository(
    @RetrofitV7ActionItem retrofit: Retrofit
  ): RemoteAppViewRepository {
    return AppViewNetworkService(retrofit.create(AppViewNetworkService.Retrofit::class.java))
  }
}