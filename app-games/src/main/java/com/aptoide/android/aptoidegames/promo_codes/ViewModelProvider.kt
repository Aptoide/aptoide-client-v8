package com.aptoide.android.aptoidegames.promo_codes

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
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val appMetaUseCase: AppMetaUseCase,
) : ViewModel()

@Composable
fun rememberPromoCodeSheetState(promoCode: PromoCode): Pair<PromoCodeSheetUiState, () -> Unit> =
  runPreviewable(
    preview = { PromoCodeSheetUiStateProvider().values.toSet().random() to {} },
    real = {
      val injectionsProvider = hiltViewModel<InjectionsProvider>()
      val vm: PromoCodeSheetViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
        key = "promoCodeBottomSheet/${promoCode.packageName}/${promoCode.code}",
        factory = object : ViewModelProvider.Factory {
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PromoCodeSheetViewModel(
              promoCode = promoCode,
              appMetaUseCase = injectionsProvider.appMetaUseCase,
            ) as T
          }
        }
      )

      val uiState by vm.uiState.collectAsState()
      uiState to vm::reload
    }
  )
