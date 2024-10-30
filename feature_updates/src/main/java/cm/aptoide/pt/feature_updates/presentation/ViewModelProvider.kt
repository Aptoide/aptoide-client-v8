package cm.aptoide.pt.feature_updates.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//TODO add needed arguments
@HiltViewModel
class InjectionsProvider @Inject constructor() : ViewModel()

//TODO add needed logic and arguments
@Composable
fun rememberUpdates():
  UpdatesUiState = runPreviewable(
  preview = {
    UpdatesUiStateProvider().values.toSet().random()
  },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: UpdatesViewModel = viewModel(
      key = "UpdatesVM", //TODO Temp
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return UpdatesViewModel() as T
        }
      }
    )
    val uiState by vm.uiState.collectAsState()
    uiState
  })
