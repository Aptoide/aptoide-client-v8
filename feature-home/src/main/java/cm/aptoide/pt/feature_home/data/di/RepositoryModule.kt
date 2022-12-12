package cm.aptoide.pt.feature_home.data.di

import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_home.data.AptoideBundlesRepository
import cm.aptoide.pt.feature_home.data.AptoideWidgetsRepository
import cm.aptoide.pt.feature_home.data.BundlesRepository
import cm.aptoide.pt.feature_home.data.WidgetsRepository
import cm.aptoide.pt.feature_home.data.network.service.WidgetsRemoteService
import cm.aptoide.pt.feature_home.domain.BundleActionMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun providesBundlesRepository(
    widgetsRepository: WidgetsRepository,
    appsRepository: AppsRepository,
    bundleActionMapper: BundleActionMapper
  ): BundlesRepository {
    return AptoideBundlesRepository(
      widgetsRepository = widgetsRepository,
      appsRepository = appsRepository,
      bundleActionMapper = bundleActionMapper,
    )
  }

  @Provides
  @Singleton
  fun providesBundleActionMapper(): BundleActionMapper {
    return BundleActionMapper()
  }

  @Provides
  @Singleton
  fun providesWidgetsRepository(
    widgetsService: WidgetsRemoteService
  ): WidgetsRepository {
    return AptoideWidgetsRepository(widgetsService)
  }
}
