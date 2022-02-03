package cm.aptoide.pt.feature_apps.data

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