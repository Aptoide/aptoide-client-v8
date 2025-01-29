package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

const val DEFAULT_AUTO_SCROLL_SPEED = 4L

class AutoScrollViewModel(scrollSpeedInSeconds: Long) : ViewModel() {

  private var currentItem: Int = 0
  private var delayTime: Long = TimeUnit.SECONDS.toMillis(scrollSpeedInSeconds)

  private val viewModelState = MutableStateFlow<Int?>(null)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  private var autoScrollJob: Job? = null
  private var isLocked = false

  fun init(
    initialItem: Int,
    delayTime: Long = DEFAULT_AUTO_SCROLL_SPEED
  ) {
    this.currentItem = initialItem
    this.delayTime = delayTime

    isLocked = false
    start()
  }

  fun start() {
    cancel()
    if (!isLocked) {
      autoScrollJob = viewModelScope.launch {
        delay(delayTime)
        viewModelState.value = ++currentItem
      }
    }
  }

  fun cancel() {
    autoScrollJob?.apply {
      if (isActive) this.cancel()
    }
  }

  fun lock() {
    isLocked = true
    cancel()
  }

  fun updateCurrentItem(item: Int) {
    currentItem = item
  }

  fun updateSpeed(scrollSpeedInSeconds: Long) {
    delayTime = TimeUnit.SECONDS.toMillis(scrollSpeedInSeconds)
    start()
  }

  override fun onCleared() {
    super.onCleared()
    cancel()
  }
}

@Composable
fun perCarouselViewModel(
  carouselTag: String,
  scrollSpeedInSeconds: Long = DEFAULT_AUTO_SCROLL_SPEED
): AutoScrollViewModel {
  val vm: AutoScrollViewModel = viewModel(
    key = carouselTag,
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AutoScrollViewModel(scrollSpeedInSeconds) as T
      }
    }
  )

  LaunchedEffect(scrollSpeedInSeconds) {
    vm.updateSpeed(scrollSpeedInSeconds)
  }

  return vm
}
