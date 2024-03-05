package cm.aptoide.pt.feature_home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_home.domain.BundlesUseCase
import cm.aptoide.pt.feature_home.domain.Type
import cm.aptoide.pt.feature_home.domain.randomBundle
import cm.aptoide.pt.feature_home.presentation.BundlesViewUiStateType.IDLE
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.reflect.KFunction0

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val urlsCache: UrlsCache,
  val bundlesUseCase: BundlesUseCase,
) : ViewModel()

@Composable
fun bundlesList(): Pair<BundlesViewUiState, () -> Unit> = runPreviewable(
  preview = {
    BundlesViewUiState(
      List(Type.values().size) { randomBundle.copy(type = Type.values()[it]) }.shuffled(),
      IDLE
    ) to {}
  },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: BundlesViewModel = viewModel(
      key = "homeAllBundles",
      factory = object : Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return BundlesViewModel(
            urlsCache = injectionsProvider.urlsCache,
            bundlesUseCase = injectionsProvider.bundlesUseCase,
            context = null
          ) as T
        }
      }
    )
    val uiState by vm.uiState.collectAsState()
    uiState to vm::loadFreshHomeBundles
  }
)

@Composable
fun bundlesList(context: String): Pair<BundlesViewUiState, KFunction0<Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: BundlesViewModel = viewModel(
    key = context,
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return BundlesViewModel(
          urlsCache = injectionsProvider.urlsCache,
          bundlesUseCase = injectionsProvider.bundlesUseCase,
          context = context
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::loadFreshHomeBundles
}
