package cm.aptoide.pt.feature_updates.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_updates.domain.Updates
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val updates: Updates
) : ViewModel()

@Composable
fun rememberUpdates():
  UpdatesUiState = runPreviewable(
  preview = {
    UpdatesUiStateProvider().values.toSet().random()
  },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: UpdatesViewModel = viewModel(
      viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return UpdatesViewModel(injectionsProvider.updates) as T
        }
      }
    )
    val uiState by vm.uiState.collectAsState()
    uiState
  })

@Composable
fun rememberCurrentUpdates():
  UpdatesUiState = runPreviewable(
  preview = {
    UpdatesUiStateProvider().values.toSet().random()
  },
  real = {
    val key = rememberSaveable { System.currentTimeMillis().toString() }

    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: UpdatesListViewModel = viewModel(
      key = key,
      viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return UpdatesListViewModel(injectionsProvider.updates) as T
        }
      }
    )
    val uiState by vm.uiState.collectAsState()
    uiState
  })
