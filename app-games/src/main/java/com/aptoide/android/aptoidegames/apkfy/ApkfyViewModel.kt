package com.aptoide.android.aptoidegames.apkfy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import cm.aptoide.pt.feature_mmp.apkfy.domain.ApkfyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApkfyViewModel @Inject constructor(
  private val apkfyManager: ApkfyManager,
  private val appMetaUseCase: AppMetaUseCase,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<App?>(value = null)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      try {
        apkfyManager.getApkfy()?.run {
          packageName?.let {
            var app = appMetaUseCase.getMetaInfo(it)

            if (oemId != null) {
              val oemIdQuery = "?oemid=${oemId}"
              app = app.copy(
                file = app.file.run {
                  copy(
                    path = path + oemIdQuery,
                    path_alt = path_alt + oemIdQuery
                  )
                }
              )
            }

            viewModelState.update { app }
          }
        }
      } catch (e: Throwable) {
        e.printStackTrace()
      }
    }
  }
}

@Composable
fun rememberApkfyApp() = runPreviewable(
  preview = { null },
  real = {
    val vm = hiltViewModel<ApkfyViewModel>()
    val app by vm.uiState.collectAsState()
    app
  }
)
