package cm.aptoide.pt.feature_editorial.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.feature_editorial.domain.usecase.EditorialsMetaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface EditorialsMetaUseCaseProvider {
  val editorialsMetaUseCase: EditorialsMetaUseCase
}

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val provider: EditorialsMetaUseCaseProvider,
) : ViewModel()

@Composable
fun editorialsMetaViewModel(requestUrl: String): EditorialsMetaViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = requestUrl,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditorialsMetaViewModel(
          editorialWidgetUrl = requestUrl,
          editorialsMetaUseCase = injectionsProvider.provider.editorialsMetaUseCase,
        ) as T
      }
    }
  )
}
