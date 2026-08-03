package cm.aptoide.pt.feature_apps.di

import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.feature_apps.data.AppMapper
import cm.aptoide.pt.feature_apps.data.AppRepository
import cm.aptoide.pt.feature_apps.data.AppsListMapper
import cm.aptoide.pt.feature_apps.data.AppsListRepository
import cm.aptoide.pt.feature_apps.data.AptoideAppMapper
import cm.aptoide.pt.feature_apps.data.AptoideAppRepository
import cm.aptoide.pt.feature_apps.data.AptoideAppsListMapper
import cm.aptoide.pt.feature_apps.data.AptoideAppsListRepository
import cm.aptoide.pt.feature_apps.data.SplitsRepository
import cm.aptoide.pt.feature_apps.data.SplitsRepositoryImpl
import cm.aptoide.pt.feature_apps.data.EmptyReviewsRepository
import cm.aptoide.pt.feature_apps.data.ReviewsRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsListRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiReviewsRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiSplitsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import java.util.Optional
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun providesAppMapper(appMapper: AptoideAppMapper): AppMapper = appMapper

  @Provides
  @Singleton
  fun providesAppsListMapper(appsListMapper: AptoideAppsListMapper): AppsListMapper = appsListMapper

  @Provides
  @Singleton
  fun providesAppRepository(
    @RetrofitV7 retrofitV7: Retrofit,
    @StoreName storeName: String,
    appMapper: AppMapper,
    deviceApi: Optional<DeviceApiAppRepository>,
  ): AppRepository = if (deviceApi.isPresent) {
    deviceApi.get()
  } else {
    AptoideAppRepository(
      appsRemoteDataSource = retrofitV7.create(AptoideAppRepository.Retrofit::class.java),
      storeName = storeName,
      mapper = appMapper,
      scope = CoroutineScope(Dispatchers.IO)
    )
  }

  @Provides
  @Singleton
  fun providesAppsRepository(
    @RetrofitV7 retrofitV7: Retrofit,
    @StoreName storeName: String,
    appsListMapper: AppsListMapper,
    deviceApi: Optional<DeviceApiAppsListRepository>,
  ): AppsListRepository = if (deviceApi.isPresent) {
    deviceApi.get()
  } else {
    AptoideAppsListRepository(
      appsRemoteDataSource = retrofitV7.create(AptoideAppsListRepository.Retrofit::class.java),
      storeName = storeName,
      mapper = appsListMapper,
      scope = CoroutineScope(Dispatchers.IO)
    )
  }

  @Provides
  @Singleton
  fun providesReviewsRepository(
    deviceApi: Optional<DeviceApiReviewsRepository>,
  ): ReviewsRepository = if (deviceApi.isPresent) deviceApi.get() else EmptyReviewsRepository()

  @Provides
  @Singleton
  fun providesSplitsRepository(
    @RetrofitV7 retrofitV7: Retrofit,
    deviceApi: Optional<DeviceApiSplitsRepository>,
  ): SplitsRepository = if (deviceApi.isPresent) {
    deviceApi.get()
  } else {
    SplitsRepositoryImpl(
      appsRemoteDataSource = retrofitV7.create(SplitsRepositoryImpl.Retrofit::class.java),
      scope = CoroutineScope(Dispatchers.IO)
    )
  }
}
