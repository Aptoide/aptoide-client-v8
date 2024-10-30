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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
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
  ): AppRepository = AptoideAppRepository(
    appsRemoteDataSource = retrofitV7.create(AptoideAppRepository.Retrofit::class.java),
    storeName = storeName,
    mapper = appMapper,
    scope = CoroutineScope(Dispatchers.IO)
  )

  @Provides
  @Singleton
  fun providesAppsRepository(
    @RetrofitV7 retrofitV7: Retrofit,
    @StoreName storeName: String,
    appsListMapper: AppsListMapper,
  ): AppsListRepository = AptoideAppsListRepository(
    appsRemoteDataSource = retrofitV7.create(AptoideAppsListRepository.Retrofit::class.java),
    storeName = storeName,
    mapper = appsListMapper,
    scope = CoroutineScope(Dispatchers.IO)
  )
}
