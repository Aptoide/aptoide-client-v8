package com.aptoide.android.aptoidegames.usage_stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
@HiltViewModel
class PackageUsageStatsViewModel @Inject constructor(
  packageUsageStatsProvider: PackageUsageStatsProvider
): ViewModel() {

  private val viewModelState = MutableStateFlow<Int?>(null)

  init {
    start()
  }

  fun start() {
    viewModelScope.launch {
      viewModelScope.coroutineContext.ensureActive()
      while(viewModelScope.isActive) {

        delay(10000L)
      }
    }
  }
}

 */
