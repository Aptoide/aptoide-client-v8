package com.aptoide.android.aptoidegames.promotions.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.hasNotificationsPermission
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.launch.AppLaunchPreferencesManager
import com.aptoide.android.aptoidegames.promotions.data.database.SkippedPromotionsRepository
import com.aptoide.android.aptoidegames.promotions.domain.CompatiblePromotionsUseCase
import com.aptoide.android.aptoidegames.promotions.domain.Promotion
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromotionsViewModel @Inject constructor(
  private val compatiblePromotionsUseCase: CompatiblePromotionsUseCase,
  private val skippedPromotionsRepository: SkippedPromotionsRepository,
  private val appLaunchPreferencesManager: AppLaunchPreferencesManager,
  @ApplicationContext private val context: Context,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<Pair<Promotion, App>?>(null)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      try {
        if (!appLaunchPreferencesManager.isFirstLaunch() && (context.hasNotificationsPermission() || !appLaunchPreferencesManager.shouldShowNotificationsDialog())) {
          val promotionData = compatiblePromotionsUseCase.getTopPromotion()
          viewModelState.update { promotionData }
        }
      } catch (t: Throwable) {
        t.printStackTrace()
      }
    }
  }

  fun dismissPromotion() {
    viewModelScope.launch {
      viewModelState.value?.first?.let {
        skippedPromotionsRepository.skipPromotion(it.packageName)
      }
    }
    viewModelState.update { null }
  }
}
