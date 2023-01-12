package cm.aptoide.pt.feature_reactions.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.feature_reactions.domain.usecase.ReactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface ReactionsUseCaseProvider {
  val reactionsUseCase: ReactionsUseCase
}

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val provider: ReactionsUseCaseProvider,
) : ViewModel()

@Composable
fun ReactionsViewModel(id: String): ReactionsViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = id,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ReactionsViewModel(
          id = id,
          reactionsUseCase = injectionsProvider.provider.reactionsUseCase,
        ) as T
      }
    }
  )
}
