package cm.aptoide.pt.feature_editorial.data.di

import cm.aptoide.pt.feature_editorial.data.AptoideEditorialRepository
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
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
  fun providesEditorialRepository(): EditorialRepository {
    return AptoideEditorialRepository()
  }
}