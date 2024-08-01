package cm.aptoide.pt.feature_apps.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import cm.aptoide.pt.feature_apps.domain.AppVersionsUseCase
import cm.aptoide.pt.feature_apps.domain.AppsByTagUseCase
import cm.aptoide.pt.feature_apps.domain.CategoryAppsUseCase
import cm.aptoide.pt.feature_apps.domain.ESkillsAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.reflect.KFunction0

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val appMetaUseCase: AppMetaUseCase,
  val appVersionsUseCase: AppVersionsUseCase,
  val appsByTagUseCase: AppsByTagUseCase,
  val eSkillsAppsUseCase: ESkillsAppsUseCase,
  val categoryAppsUseCase: CategoryAppsUseCase,
) : ViewModel()

@Composable
fun rememberApp(
  packageName: String,
): Pair<AppUiState, () -> Unit> = runPreviewable(
  preview = { AppUiStateProvider().values.toSet().random() to {} },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: AppViewModel = viewModel(
      viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
      key = "appView/$packageName",
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return AppViewModel(
            appMetaUseCase = injectionsProvider.appMetaUseCase,
            source = packageName.toPackageNameParam(),
          ) as T
        }
      }
    )

    val uiState by vm.uiState.collectAsState()
    uiState to vm::reload
  }
)

@Composable
fun rememberAppBySource(
  source: String,
): Pair<AppUiState, () -> Unit> = runPreviewable(
  preview = { AppUiStateProvider().values.toSet().random() to {} },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: AppViewModel = viewModel(
      viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
      key = "appView/$source",
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return AppViewModel(
            appMetaUseCase = injectionsProvider.appMetaUseCase,
            source = source,
          ) as T
        }
      }
    )

    val uiState by vm.uiState.collectAsState()
    uiState to vm::reload
  }
)

@Composable
fun appVersions(packageName: String): Pair<AppsListUiState, KFunction0<Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: AppsListViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
    key = "appVersions/$packageName",
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AppsListViewModel(
          source = packageName,
          appsListUseCase = injectionsProvider.appVersionsUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()

  return uiState to vm::reload
}

@Composable
fun rememberAppsByTag(
  tag: String,
  salt: String? = null,
): Pair<AppsListUiState, () -> Unit> = runPreviewable(
  preview = {
    AppsListUiState.Idle(List((0..50).random()) { randomApp }) to {}
  }, real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: AppsListViewModel = viewModel(
      key = "tagApps/$tag/$salt",
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return AppsListViewModel(
            source = tag,
            appsListUseCase = injectionsProvider.appsByTagUseCase,
          ) as T
        }
      }
    )
    val uiState by vm.uiState.collectAsState()
    uiState to vm::reload
  }
)

@Suppress("unused")
@Composable
fun eSkillsApps(
  tag: String,
  salt: String? = null,
): Pair<AppsListUiState, KFunction0<Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: AppsListViewModel = viewModel(
    key = "eSkillsApps/$tag/$salt",
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AppsListViewModel(
          source = tag,
          appsListUseCase = injectionsProvider.eSkillsAppsUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()

  return uiState to vm::reload
}

@Composable
fun categoryApps(
  categoryName: String,
  salt: String? = null,
): Pair<AppsListUiState, KFunction0<Unit>> {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  val vm: AppsListViewModel = viewModel(
    key = "categoryApps/$categoryName/$salt",
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AppsListViewModel(
          source = categoryName,
          appsListUseCase = injectionsProvider.categoryAppsUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()

  return uiState to vm::reload
}

fun String.toPackageNameParam() = "package_name=$this"
