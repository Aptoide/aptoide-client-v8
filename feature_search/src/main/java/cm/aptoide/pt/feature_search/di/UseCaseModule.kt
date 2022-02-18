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
  fun provideGetSearchSuggestionsUseCase(searchRepository: SearchRepository): GetSearchSuggestionsCase {
    return GetSearchSuggestionsCase(searchRepository)
  }

  @Provides
  fun provideGetLocalTopDownloadedAppsUseCase(): GetLocalTopDownloadedAppsUseCase {
    return GetLocalTopDownloadedAppsUseCase()
  }

  @Provides
  fun provideGetSearchAutoCompleteUseCase(searchRepository: SearchRepository): GetSearchAutoCompleteUseCase {
    return GetSearchAutoCompleteUseCase(searchRepository)
  }

  @Provides
  fun provideGetTopSearchedAppsUseCase(): GetTopSearchedAppsUseCase {
    return GetTopSearchedAppsUseCase()
  }


  @Provides
  fun provideSaveSearchHistoryUseCase(searchRepository: SearchRepository): SaveSearchHistoryUseCase {
    return SaveSearchHistoryUseCase(searchRepository)
  }

  @Provides
  fun provideSearchAppUseCase(searchRepository: SearchRepository): SearchAppUseCase {
    return SearchAppUseCase(searchRepository)
  }


}