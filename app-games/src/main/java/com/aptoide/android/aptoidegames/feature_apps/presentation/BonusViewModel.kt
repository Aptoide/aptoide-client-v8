package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_bonus.data.BonusData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BonusViewModel @Inject constructor() : ViewModel() {

  val uiState = BonusData.data
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      BonusData.data.value
    )

}

@Composable
fun rememberBonusBundle() = runPreviewable(
  preview = { "" to "" },
  real = {
    val vm = hiltViewModel<BonusViewModel>()
    val bonusInfo by vm.uiState.collectAsState()
    bonusInfo
  }
)
