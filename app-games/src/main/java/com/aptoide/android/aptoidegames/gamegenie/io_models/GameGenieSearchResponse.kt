package com.aptoide.android.aptoidegames.gamegenie.io_models

import androidx.annotation.Keep
import cm.aptoide.pt.feature_apps.data.model.AppJSON

@Keep
data class GameGenieSearchResponse(
  val list: List<AppJSON>
)