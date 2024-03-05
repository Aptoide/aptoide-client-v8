package cm.aptoide.pt.feature_editorial.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.domain.randomArticleMeta
import cm.aptoide.pt.feature_editorial.domain.usecase.ArticleUseCase
import cm.aptoide.pt.feature_editorial.domain.usecase.ArticlesMetaUseCase
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
fun rememberEditorialsCardViewModel(
  tag: String,
  subtype: String? = null,
  salt: String? = null
): Pair<List<ArticleMeta>?, String> = runPreviewable(
  preview = {
    List((0..50).random()) { randomArticleMeta }.shuffled() to "adlistIDpreview"
  },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: EditorialsCardViewModel = viewModel(
      key = tag + subtype + salt,
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return EditorialsCardViewModel(
            tag = tag,
            subtype = subtype,
            articlesMetaUseCase = injectionsProvider.articlesMetaUseCase,
          ) as T
        }
      }
    )
    val uiState by vm.uiState.collectAsState()
    uiState to vm.adListId
  })

@Composable
fun relatedEditorialsCardViewModel(packageName: String): RelatedEditorialsCardViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = "relatedEditorials/$packageName",
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
fun editorialViewModel(articleId: String): EditorialViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = articleId,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditorialViewModel(
          articleId = articleId,
          articleUseCase = injectionsProvider.articleUseCase,
        ) as T
      }
    }
  )
}
