package cm.aptoide.pt.feature_appview.di

import cm.aptoide.pt.feature_appview.domain.repository.AppViewRepository
import cm.aptoide.pt.feature_appview.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

  @Provides
  fun provideGetAppInfoUseCase(appViewRepository: AppViewRepository): GetAppInfoUseCase {
    return GetAppInfoUseCase(appViewRepository)
  }

  @Provides
  fun provideGetAppCoinsAppsUseCase(appViewRepository: AppViewRepository): GetAppCoinsAppsUseCase {
    return GetAppCoinsAppsUseCase()
  }

  @Provides
  fun provideGetAppOtherVersionsUseCase(appViewRepository: AppViewRepository): GetAppOtherVersionsUseCase {
    return GetAppOtherVersionsUseCase()
  }

  @Provides
  fun provideGetRelatedContentUseCase(appViewRepository: AppViewRepository): GetRelatedContentUseCase {
    return GetRelatedContentUseCase()
  }

  @Provides
  fun provideGetReviewsUseCase(appViewRepository: AppViewRepository): GetReviewsUseCase {
    return GetReviewsUseCase()
  }

  @Provides
  fun provideGetSimilarAppsUseCase(appViewRepository: AppViewRepository): GetSimilarAppsUseCase {
    return GetSimilarAppsUseCase()
  }

  @Provides
  fun provideReportAppUseCase(appViewRepository: AppViewRepository): ReportAppUseCase {
    return ReportAppUseCase()
  }

  @Provides
  fun provideSetAppReviewUseCase(appViewRepository: AppViewRepository): SetAppReviewUseCase {
    return SetAppReviewUseCase()
  }

  @Provides
  fun provideShareAppUseCase(appViewRepository: AppViewRepository): ShareAppUseCase {
    return ShareAppUseCase()
  }

}