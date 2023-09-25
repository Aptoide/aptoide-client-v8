package cm.aptoide.pt.feature_oos.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_oos.domain.AvailableSpaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelInjectionsProvider
@Inject constructor(
  val availableSpaceUseCase: AvailableSpaceUseCase,
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
fun rememberInstalledAppsListState(): InstalledAppsUiState = runPreviewable(
  preview = { InstalledAppsUiState.Loading },
  real = {
    val viewModel = hiltViewModel<InstalledAppsListViewModel>()
    val uiState by viewModel.installedAppsState.collectAsState()
    uiState
  }
)
