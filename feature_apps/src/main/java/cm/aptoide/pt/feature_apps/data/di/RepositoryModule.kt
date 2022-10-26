package cm.aptoide.pt.feature_apps.data.di

import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.feature_apps.data.*
import cm.aptoide.pt.feature_apps.data.network.service.AppsRemoteService
import cm.aptoide.pt.feature_apps.data.network.service.AptoideAppsNetworkService
import cm.aptoide.pt.feature_apps.data.network.service.WidgetsNetworkService
import cm.aptoide.pt.feature_apps.data.network.service.WidgetsRemoteService
import cm.aptoide.pt.feature_apps.domain.BundleActionMapper
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
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
  fun providesBundlesRepository(
    widgetsRepository: WidgetsRepository,
    appsRepository: AppsRepository,
    editorialRepository: EditorialRepository,
    reactionsManager: ReactionsRepository,
    bundleActionMapper: BundleActionMapper
  ): BundlesRepository {
    return AptoideBundlesRepository(
      widgetsRepository = widgetsRepository,
      appsRepository = appsRepository,
      editorialRepository = editorialRepository,
      reactionsRepository = reactionsManager,
      bundleActionMapper = bundleActionMapper
    )
  }

  @Provides
  @Singleton
  fun providesBundleActionMapper(): BundleActionMapper {
    return BundleActionMapper()
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
  fun providesWidgetsRemoteService(
    @RetrofitV7 retrofitV7: Retrofit,
    @StoreName storeName: String
  ): WidgetsRemoteService = WidgetsNetworkService(
    widgetsRemoteDataSource = retrofitV7.create(WidgetsNetworkService.Retrofit::class.java),
    storeName = storeName
  )

  @Provides
  @Singleton
  fun providesAppsRemoteService(
    @RetrofitV7 retrofitV7: Retrofit,
    @StoreName storeName: String
  ): AppsRemoteService {
    return AptoideAppsNetworkService(
      appsRemoteDataSource = retrofitV7.create(AptoideAppsNetworkService.Retrofit::class.java),
      storeName = storeName
    )
  }
}