package com.aptoide.android.aptoidegames.feature_ad

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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
   val mintegral: Mintegral,
   val appMetaUseCase: AppMetaUseCase,
) : ViewModel()

@Composable
fun rememberAd(adClick: (String) -> Unit): MintegralAdApp? = runPreviewable(
  preview = { MintegralAdApp(randomApp, {}) },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: AdViewModel = viewModel(
      viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
      key = "adViewModel",
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return AdViewModel(
            mintegral = injectionsProvider.mintegral,
            appMetaUseCase = injectionsProvider.appMetaUseCase,
            adClick = adClick
          ) as T
        }
      }
    )

    val uiState by vm.uiState.collectAsState()
    uiState
  }
)
