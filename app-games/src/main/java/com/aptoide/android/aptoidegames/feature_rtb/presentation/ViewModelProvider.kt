package com.aptoide.android.aptoidegames.feature_rtb.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import com.aptoide.android.aptoidegames.feature_rtb.repository.RTBRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val repository: RTBRepository,
) : ViewModel()

@Composable
fun rememberRTBApps(
  tag: String,
  salt: String? = null,
): Pair<AppsListUiState, () -> Unit> = runPreviewable(
  preview = {
    AppsListUiState.Idle(List((0..50).random()) { randomApp }) to {}
  }, real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: RTBAppListViewModel = viewModel(
      key = "rtb/$tag/$salt",
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return RTBAppListViewModel(
            repository = injectionsProvider.repository,
          ) as T
        }
      }
    )
    val uiState by vm.uiState.collectAsState()
    uiState to vm::reload
  }
)
