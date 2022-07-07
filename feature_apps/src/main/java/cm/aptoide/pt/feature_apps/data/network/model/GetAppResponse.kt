package cm.aptoide.pt.feature_apps.data.network.model

internal data class GetAppResponse(var nodes: Nodes) : BaseV7Response()

internal data class Nodes(
  var meta: GetAppMeta
)

internal data class GetAppMeta(val data: AppJSON)

