package cm.aptoide.pt.feature_reactions.di

import cm.aptoide.pt.feature_reactions.domain.usecase.ReactionsUseCase
import cm.aptoide.pt.feature_reactions.presentation.ReactionsUseCaseProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

  @Provides
  fun provideReactionsUseCaseProvider(
    reactionsUseCase: ReactionsUseCase
  ): ReactionsUseCaseProvider =
    object : ReactionsUseCaseProvider {
      override val reactionsUseCase: ReactionsUseCase = reactionsUseCase
    }
}
