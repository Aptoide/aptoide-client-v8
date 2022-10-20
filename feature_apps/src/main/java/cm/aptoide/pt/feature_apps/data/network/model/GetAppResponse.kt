package cm.aptoide.pt.feature_apps.data.network.model

import androidx.annotation.Keep

@Keep
internal data class GetAppResponse(var nodes: Nodes) : BaseV7Response()

@Keep
internal data class Nodes(
  var meta: GetAppMeta
)

@Keep
internal data class GetAppMeta(val data: AppJSON)

