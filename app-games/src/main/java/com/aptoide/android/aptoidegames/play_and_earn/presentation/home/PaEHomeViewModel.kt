package com.aptoide.android.aptoidegames.play_and_earn.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaEHomeViewModel @Inject constructor(
  private val paEPreferencesRepository: PaEPreferencesRepository
) : ViewModel() {

  suspend fun hasShownHeaderBundle() = paEPreferencesRepository.hasShownHeaderBundle()

  fun setHeaderBundleShown() {
    viewModelScope.launch {
      paEPreferencesRepository.setHeaderBundleShown()
    }
  }
}

@Composable
fun rememberPaEHeaderState(): Pair<Boolean?, () -> Unit> = runPreviewable(
  preview = { Pair(false, {}) },
  real = {
    val vm = hiltViewModel<PaEHomeViewModel>()
    var hasShownHeader: Boolean? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
      hasShownHeader = vm.hasShownHeaderBundle()
    }

    hasShownHeader to vm::setHeaderBundleShown
  }
)
