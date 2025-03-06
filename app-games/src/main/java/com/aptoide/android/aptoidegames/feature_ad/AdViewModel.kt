package com.aptoide.android.aptoidegames.feature_ad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import cm.aptoide.pt.feature_apps.domain.AppSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class AdViewModel(
  private val mintegral: Mintegral,
  private val adClick: (String) -> Unit,
  private val appMetaUseCase: AppMetaUseCase,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<MintegralAdApp?>(null)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      viewModelState.update { null }
      try {
        mintegral.initNativeAd(adClick = adClick).collect { newCampaign ->
          if(newCampaign != null){
            val app = appMetaUseCase
              .getMetaInfo(source = AppSource.of(null, newCampaign.packageName).asSource())
            viewModelState.update {
              MintegralAdApp(app = app, register = { view ->
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
}
