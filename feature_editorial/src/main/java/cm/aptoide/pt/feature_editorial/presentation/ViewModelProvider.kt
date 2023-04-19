package cm.aptoide.pt.feature_editorial.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.feature_editorial.domain.usecase.ArticlesMetaUseCase
import cm.aptoide.pt.feature_editorial.domain.usecase.ArticleUseCase
import cm.aptoide.pt.feature_editorial.domain.usecase.RelatedArticlesMetaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val articlesMetaUseCase: ArticlesMetaUseCase,
  val articleUseCase: ArticleUseCase,
  val relatedArticlesMetaUseCase: RelatedArticlesMetaUseCase,
) : ViewModel()

@Composable
fun EditorialsCardViewModel(
  requestUrl: String,
  subtype: String? = null,
  salt: String? = null
): EditorialsCardViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = requestUrl + subtype + salt,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditorialsCardViewModel(
          editorialWidgetUrl = requestUrl,
          subtype = subtype,
          articlesMetaUseCase = injectionsProvider.articlesMetaUseCase,
        ) as T
      }
    }
  )
}

@Composable
fun RelatedEditorialsCardViewModel(packageName: String): RelatedEditorialsCardViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = packageName,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return RelatedEditorialsCardViewModel(
          packageName = packageName,
          relatedArticlesMetaUseCase = injectionsProvider.relatedArticlesMetaUseCase,
        ) as T
      }
    }
  )
}

@Composable
fun EditorialViewModel(articleId: String, editorialUrl: String): EditorialViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = articleId,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditorialViewModel(
          articleId = articleId,
          editorialUrl = editorialUrl,
          articleUseCase = injectionsProvider.articleUseCase,
        ) as T
      }
    }
  )
}
