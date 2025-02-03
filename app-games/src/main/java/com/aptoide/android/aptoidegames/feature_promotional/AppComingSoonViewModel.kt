package com.aptoide.android.aptoidegames.feature_promotional

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptoide.android.aptoidegames.feature_promotional.AppComingSoonUIState.Idle
import com.aptoide.android.aptoidegames.feature_promotional.AppComingSoonUIState.Loading
import com.aptoide.android.aptoidegames.feature_promotional.domain.AppComingSoonManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppComingSoonViewModel @Inject constructor(
  private val cardUrl: String,
  private val appComingSoonManager: AppComingSoonManager
) : ViewModel() {

  private val viewModelState = MutableStateFlow<AppComingSoonUIState>(Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      viewModelState.update { Loading }
      appComingSoonManager.loadAppComingSoonCard(cardUrl)
        .catch {
          it.printStackTrace()
          viewModelState.update { AppComingSoonUIState.Error }
        }
        .collect { card -> viewModelState.update { Idle(card) } }
    }
  }

  fun updateNotifyMe(packageName: String, isSubscribed: Boolean) {
    CoroutineScope(Dispatchers.IO).launch {
      appComingSoonManager.updateSubscribedApp(packageName, isSubscribed)
    }
  }
}
