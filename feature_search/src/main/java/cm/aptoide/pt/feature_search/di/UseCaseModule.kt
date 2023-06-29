package cm.aptoide.pt.feature_search.di

import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
  @Provides
  fun provideSearchUseCase(searchRepository: SearchRepository): SearchUseCase {
    return SearchUseCase(searchRepository)
  }
}