package cm.aptoide.pt.feature_oos.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_oos.di.UninstallPackagesFilter
import cm.aptoide.pt.feature_oos.domain.InstalledAppsUseCase
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoManager
import cm.aptoide.pt.install_manager.InstallManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelInjectionsProvider
@Inject constructor(
  val installManager: InstallManager,
  val installedAppsUseCase: InstalledAppsUseCase,
  val installPackageInfoManager: InstallPackageInfoManager,
  @UninstallPackagesFilter val uninstallPackagesFilter: List<String>,
) : ViewModel()

@Composable
fun rememberAvailableSpaceState(app: App): Long = runPreviewable(
  preview = { 500000L },
  real = {
    val injectionsProvider = hiltViewModel<ViewModelInjectionsProvider>()
    val vm: AvailableSpaceViewModel = viewModel(
      key = "${app.packageName}${app.versionCode}",
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return AvailableSpaceViewModel(
            installManager = injectionsProvider.installManager,
            app = app,
            installPackageInfoManager = injectionsProvider.installPackageInfoManager,
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
            val completePackagesToFilter =
              injectionsProvider.uninstallPackagesFilter.toMutableList()
            completePackagesToFilter.add(packageName)
            @Suppress("UNCHECKED_CAST")
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
