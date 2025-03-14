package com.aptoide.android.aptoidegames.feature_ad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import cm.aptoide.pt.feature_apps.domain.AppSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class AdViewModel(
  private val mintegral: Mintegral,
  private val appMetaUseCase: AppMetaUseCase,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<MintegralAd?>(null)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  private val _mintegralAdEvents = MutableSharedFlow<MintegralAdEvent>()
  val mintegralAdEvents: Flow<MintegralAdEvent> = _mintegralAdEvents

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      viewModelState.update { null }
      try {
        mintegral.initNativeAd(adClick = ::handleAdClick).collect { newCampaign ->
          if (newCampaign != null) {
            val app = appMetaUseCase
              .getMetaInfo(source = AppSource.of(null, newCampaign.packageName).asSource())
            viewModelState.update {
              MintegralAd(app = app, register = { view ->
                mintegral.registerNativeAdView(view, newCampaign)
              })
            }
          }
        }
      } catch (e: Throwable) {
        Timber.w(e)
      }
    }
  }

  fun handleAdClick(packageName: String) {
    viewModelScope.launch {
      _mintegralAdEvents.emit(MintegralAdEvent.AdClick(packageName = packageName))
    }
  }
}
