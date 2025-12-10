package com.aptoide.android.aptoidegames.feature_rtb.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptoide.android.aptoidegames.feature_rtb.repository.RTBRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RTBAdViewModel @Inject constructor(
  private val rtbRepository: RTBRepository
) : ViewModel() {

  private val viewModelState = MutableStateFlow<AdClickResult>(AdClickResult.Loading("Loading..."))
  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  fun onAdCampaignClick(trackingUrl: String, onResult: (AdClickResult) -> Unit) {
    viewModelScope.launch {
      try {
        val result = rtbRepository.resolveAdRedirects(trackingUrl)
        val adClickResult = result.fold(
          onSuccess = { finalUrl ->
            val successResult = AdClickResult.Success(finalUrl)
            viewModelState.update { successResult }
            successResult
          },
          onFailure = { error ->
            Timber.e(error, "ViewModel: Failed to resolve ad URL: $trackingUrl")
            val errorResult = AdClickResult.Error(error.message ?: "Unknown error")
            viewModelState.update { errorResult }
            errorResult
          }
        )
        onResult(adClickResult)
      } catch (e: Exception) {
        Timber.e(e, "ViewModel: Exception in onAdCampaignClickAsync")
        val errorResult = AdClickResult.Error(e.message ?: "Unknown error")
        onResult(errorResult)
      }
    }
  }
}

sealed class AdClickResult {
  data class Success(val finalUrl: String) : AdClickResult()
  data class Loading(val message: String) : AdClickResult()
  data class Error(val message: String) : AdClickResult()
}
