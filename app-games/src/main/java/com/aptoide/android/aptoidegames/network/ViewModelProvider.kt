package com.aptoide.android.aptoidegames.network

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.network.presentation.NetworkPreferencesViewModel
import com.aptoide.android.aptoidegames.network.repository.NetworkPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val networkPreferencesManager: NetworkPreferencesRepository,
) : ViewModel()

@Composable
fun rememberDownloadOverWifi(): Boolean = runPreviewable(
  preview = { false },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: NetworkPreferencesViewModel = viewModel(
      key = "networkpreferences",
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return NetworkPreferencesViewModel(
            networkPreferencesManager = injectionsProvider.networkPreferencesManager,
          ) as T
        }
      }
    )
    val uiState by vm.downloadOnlyOverWifi.collectAsState()

    uiState
  }
)
