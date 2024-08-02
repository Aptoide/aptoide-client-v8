package cm.aptoide.pt.feature_apps.data.model

import androidx.annotation.Keep
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7Response

@Keep
internal data class GetMetaResponse(var data: AppJSON) : BaseV7Response()
