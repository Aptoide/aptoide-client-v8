package com.aptoide.android.aptoidegames.feature_promotional

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.feature_promotional.AppComingSoonUIState.Loading
import com.aptoide.android.aptoidegames.feature_promotional.domain.AppComingSoonManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val appComingSoonManager: AppComingSoonManager
) : ViewModel()

@Composable
fun rememberAppComingSoon(url: String): Pair<AppComingSoonUIState, (String, Boolean) -> Unit> =
  runPreviewable(
    preview = { Loading to { _, _ -> } },
    real = {
      val injectionsProvider = hiltViewModel<InjectionsProvider>()
      val vm: AppComingSoonViewModel = viewModel(
        key = "appComingSoon" + url.hashCode(),
        factory = object : ViewModelProvider.Factory {
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AppComingSoonViewModel(
              cardUrl = url,
              appComingSoonManager = injectionsProvider.appComingSoonManager
            ) as T
          }
        }
      )
      val uiState by vm.uiState.collectAsState()
      uiState to vm::updateNotifyMe
    }
  )
