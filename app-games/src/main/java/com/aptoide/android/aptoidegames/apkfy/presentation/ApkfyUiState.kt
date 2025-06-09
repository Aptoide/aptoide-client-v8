package com.aptoide.android.aptoidegames.apkfy.presentation

import cm.aptoide.pt.feature_apps.data.App

sealed class ApkfyUiState(val app: App) {
  class Default(app: App) : ApkfyUiState(app)
  class Baseline(app: App) : ApkfyUiState(app)
  class VariantA(app: App) : ApkfyUiState(app)
  class RobloxBaseline(app: App) : ApkfyUiState(app)
  class RobloxVariantA(app: App) : ApkfyUiState(app)
}
