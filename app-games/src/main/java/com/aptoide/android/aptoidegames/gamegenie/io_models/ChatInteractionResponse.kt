package com.aptoide.android.aptoidegames.gamegenie.io_models

import androidx.annotation.Keep
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7Response
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import com.aptoide.android.aptoidegames.gamegenie.domain.UserMessage

@Keep
data class ChatInteractionResponse(
  val gpt: String,
  val user: UserMessage?,
  val video: String?,
  val apps: List<AppJSON>?,
)

@Keep
data class GetAppResponse(var nodes: Nodes) : BaseV7Response()

@Keep
data class Nodes(
  var meta: GetAppMeta,
)

@Keep
data class GetAppMeta(val data: AppJSON)
