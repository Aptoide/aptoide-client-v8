package cm.aptoide.pt.feature_report_app.data.di

import cm.aptoide.pt.feature_report_app.domain.usecase.ReportAppUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

  @Provides
  fun provideReportAppUseCase(): ReportAppUseCase {
    return ReportAppUseCase()
  }
}