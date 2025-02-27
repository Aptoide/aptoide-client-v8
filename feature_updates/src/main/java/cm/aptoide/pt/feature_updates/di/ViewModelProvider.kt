package cm.aptoide.pt.feature_updates.di

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_updates.presentation.UpdatesPreferencesViewModel
import cm.aptoide.pt.feature_updates.repository.UpdatesPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val updatesPreferencesRepository: UpdatesPreferencesRepository
) : ViewModel()

@Composable
fun rememberAutoUpdate(): Pair<Boolean?, (Boolean) -> Unit> = runPreviewable(
  preview = { false to {} },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: UpdatesPreferencesViewModel = viewModel(
      key = "updatepreferences",
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return UpdatesPreferencesViewModel(
            updatesPreferencesRepository = injectionsProvider.updatesPreferencesRepository,
          ) as T
        }
      }
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val uiState by vm.shouldAutoUpdateGames.collectAsState()
      uiState to vm::setAutoUpdateGames
    } else {
      null to { _ -> }
    }
  }
)
