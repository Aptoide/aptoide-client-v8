package cm.aptoide.pt.feature_editorial.di

import cm.aptoide.pt.aptoide_network.di.RetrofitV7ActionItem
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.feature_apps.data.AppMapper
import cm.aptoide.pt.feature_editorial.data.AptoideEditorialRepository
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.data.deviceapi.DeviceApiEditorialRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import java.util.Optional
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun providesEditorialRepository(
    mapper: AppMapper,
    @RetrofitV7ActionItem retrofit: Retrofit,
    @StoreName storeName: String,
    deviceApi: Optional<DeviceApiEditorialRepository>,
  ): EditorialRepository = if (deviceApi.isPresent) {
    deviceApi.get()
  } else {
    AptoideEditorialRepository(
      mapper = mapper,
      editorialRemoteDataSource = retrofit.create(AptoideEditorialRepository.Retrofit::class.java),
      storeName = storeName
    )
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultEditorialUrl
