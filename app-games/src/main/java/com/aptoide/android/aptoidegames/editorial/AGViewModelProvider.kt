package com.aptoide.android.aptoidegames.editorial

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.di.DefaultEditorialUrl
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.domain.randomArticleMeta
import cm.aptoide.pt.feature_editorial.domain.usecase.ArticleUseCase
import cm.aptoide.pt.feature_editorial.domain.usecase.ArticlesMetaUseCase
import cm.aptoide.pt.feature_editorial.domain.usecase.RelatedArticlesMetaUseCase
import cm.aptoide.pt.feature_editorial.presentation.ArticleListUiState
import cm.aptoide.pt.feature_editorial.presentation.EditorialViewModel
import cm.aptoide.pt.feature_editorial.presentation.EditorialsListViewModel
import cm.aptoide.pt.feature_editorial.presentation.RelatedEditorialsCardViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class AGInjectionsProvider @Inject constructor(
  editorialRepository: EditorialRepository,
  urlsCache: UrlsCache,
  @DefaultEditorialUrl defaultEditorialUrl: String,
  val articleUseCase: ArticleUseCase,
) : ViewModel() {

  private val agRepository = AGEditorialRepository(editorialRepository)

  val articlesMetaUseCase = ArticlesMetaUseCase(agRepository, urlsCache, defaultEditorialUrl)
  val relatedArticlesMetaUseCase = RelatedArticlesMetaUseCase(agRepository, urlsCache)
}

@Composable
fun rememberEditorialListState(
  tag: String,
  subtype: String? = null,
  salt: String? = null,
): Pair<ArticleListUiState, () -> Unit> = runPreviewable(
  preview = {
    ArticleListUiState.Idle(List((0..50).random()) { randomArticleMeta }) to {}
  },
  real = {
    val injectionsProvider = hiltViewModel<AGInjectionsProvider>()
    val vm: EditorialsListViewModel = viewModel(
      key = tag + subtype + salt,
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return EditorialsListViewModel(
            tag = tag,
            subtype = subtype,
            articlesMetaUseCase = injectionsProvider.articlesMetaUseCase,
          ) as T
        }
      }
    )
    val uiState by vm.uiState.collectAsState()
    uiState to vm::reload
  }
)

@Composable
fun relatedEditorialsCardViewModel(packageName: String): RelatedEditorialsCardViewModel {
  val injectionsProvider = hiltViewModel<AGInjectionsProvider>()
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
fun rememberRelatedEditorials(packageName: String): List<ArticleMeta>? = runPreviewable(
  preview = { List((0..20).random()) { randomArticleMeta } },
  real = {
    val injectionsProvider = hiltViewModel<AGInjectionsProvider>()
    val vm: RelatedEditorialsCardViewModel = viewModel(
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
    val uiState by vm.uiState.collectAsState()
    uiState
  }
)

@Composable
fun editorialViewModel(source: String): EditorialViewModel {
  val injectionsProvider = hiltViewModel<AGInjectionsProvider>()
  return viewModel(
    key = source,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditorialViewModel(
          source = source,
          articleUseCase = injectionsProvider.articleUseCase,
        ) as T
      }
    }
  )
}
