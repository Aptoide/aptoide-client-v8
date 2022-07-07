package cm.aptoide.pt.feature_apps.data.di

import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.feature_apps.data.*
import cm.aptoide.pt.feature_apps.data.network.service.AppsRemoteService
import cm.aptoide.pt.feature_apps.data.network.service.AptoideAppsNetworkService
import cm.aptoide.pt.feature_apps.data.network.service.WidgetsNetworkService
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
  fun providesBundlesRepository(
    widgetsRepository: WidgetsRepository,
    appsRepository: AppsRepository,
  ): BundlesRepository {
    return AptoideBundlesRepository(widgetsRepository, appsRepository)
  }

  @Provides
  @Singleton
  fun providesWidgetsRepository(widgetsService: WidgetsRemoteService): WidgetsRepository {
    return AptoideWidgetsRepository(widgetsService)
  }

  @Provides
  @Singleton
  fun providesAppsRepository(appsService: AppsRemoteService): AppsRepository {
    return AptoideAppsRepository(appsService)
  }

  @Provides
  @Singleton
  fun providesWidgetsRemoteService(@RetrofitV7 retrofitV7: Retrofit): WidgetsRemoteService {
    return WidgetsNetworkService(retrofitV7.create(WidgetsNetworkService.Retrofit::class.java))
  }

  @Provides
  @Singleton
  fun providesAppsRemoteService(@RetrofitV7 retrofitV7: Retrofit): AppsRemoteService {
    return AptoideAppsNetworkService(retrofitV7.create(AptoideAppsNetworkService.Retrofit::class.java))
  }
}