package cm.aptoide.pt.feature_oos.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_oos.UninstallPackagesFilter
import cm.aptoide.pt.feature_oos.domain.AvailableSpaceUseCase
import cm.aptoide.pt.feature_oos.domain.InstalledAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelInjectionsProvider
@Inject constructor(
  val availableSpaceUseCase: AvailableSpaceUseCase,
  val installedAppsUseCase: InstalledAppsUseCase,
  @UninstallPackagesFilter val uninstallPackagesFilter: List<String>
) : ViewModel()

@Composable
fun rememberAvailableSpaceState(appSize: Long): Long = runPreviewable(
  preview = { 500000L },
  real = {
    val injectionsProvider = hiltViewModel<ViewModelInjectionsProvider>()
    val vm: AvailableSpaceViewModel = viewModel(
      key = appSize.toString(),
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return AvailableSpaceViewModel(
            availableSpaceUseCase = injectionsProvider.availableSpaceUseCase,
            appSize = appSize
          ) as T
        }
      }
    )

    val uiState by vm.availableSpaceState.collectAsState()
    uiState
  }
)

@Composable
fun rememberInstalledAppsListState(packageName: String = ""): InstalledAppsUiState =
  runPreviewable(
    preview = { InstalledAppsUiState.Loading },
    real = {
      val injectionsProvider = hiltViewModel<ViewModelInjectionsProvider>()
      val vm: InstalledAppsListViewModel = viewModel(
        key = "InstalledApps$packageName",
        factory = object : ViewModelProvider.Factory {
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            val completePackagesToFilter = injectionsProvider.uninstallPackagesFilter.toMutableList()
            completePackagesToFilter.add(packageName)
            return InstalledAppsListViewModel(
              installedAppsUseCase = injectionsProvider.installedAppsUseCase,
              filterPackages = completePackagesToFilter
            ) as T
          }
        }
      )

      val uiState by vm.installedAppsState.collectAsState()
      uiState
    }
  )
