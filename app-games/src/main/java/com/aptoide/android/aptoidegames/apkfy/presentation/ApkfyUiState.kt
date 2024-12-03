package com.aptoide.android.aptoidegames.apkfy.presentation

import cm.aptoide.pt.feature_apps.data.App

sealed class ApkfyUiState(val app: App) {
  class Default(app: App): ApkfyUiState(app)
  class VariantA(app: App): ApkfyUiState(app)
  class VariantB(app: App): ApkfyUiState(app)
}
