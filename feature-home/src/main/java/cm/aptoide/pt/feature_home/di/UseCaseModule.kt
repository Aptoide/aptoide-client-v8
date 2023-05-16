package cm.aptoide.pt.feature_home.di

import cm.aptoide.pt.aptoide_network.domain.UrlsCacheInitializer
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_home.data.WidgetsRepository
import cm.aptoide.pt.feature_home.domain.AptoideUrlsCacheInitializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object UseCaseModule {

  @Provides
  @Singleton
  fun providesUrlsCacheInitializer(
    widgetsRepository: WidgetsRepository,
    articlesRepository: EditorialRepository
  ): UrlsCacheInitializer = AptoideUrlsCacheInitializer(
    widgetsRepository = widgetsRepository,
    articlesRepository = articlesRepository
  )
}
