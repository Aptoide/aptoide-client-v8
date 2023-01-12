package cm.aptoide.pt.feature_editorial.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.feature_editorial.domain.usecase.EditorialsMetaUseCase
import cm.aptoide.pt.feature_editorial.domain.usecase.GetEditorialDetailUseCase
import cm.aptoide.pt.feature_editorial.domain.usecase.RelatedEditorialsMetaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface EditorialDependenciesProvider {
  val editorialsMetaUseCase: EditorialsMetaUseCase
  val getEditorialDetailUseCase: GetEditorialDetailUseCase
  val relatedEditorialsMetaUseCase: RelatedEditorialsMetaUseCase
}

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val provider: EditorialDependenciesProvider,
) : ViewModel()

@Composable
fun EditorialsMetaViewModel(requestUrl: String, subtype: String? = null): EditorialsMetaViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = requestUrl + subtype,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditorialsMetaViewModel(
          editorialWidgetUrl = requestUrl,
          subtype = subtype,
          editorialsMetaUseCase = injectionsProvider.provider.editorialsMetaUseCase,
        ) as T
      }
    }
  )
}

@Composable
fun EditorialsMetaViewModel(packageName: String): RelatedEditorialsMetaViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = packageName,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return RelatedEditorialsMetaViewModel(
          packageName = packageName,
          relatedEditorialsMetaUseCase = injectionsProvider.provider.relatedEditorialsMetaUseCase,
        ) as T
      }
    }
  )
}

@Composable
fun EditorialViewModel(articleId: String): EditorialViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = articleId,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditorialViewModel(
          articleId = articleId,
          getEditorialDetailUseCase = injectionsProvider.provider.getEditorialDetailUseCase,
        ) as T
      }
    }
  )
}
