package cm.aptoide.pt.context

import cm.aptoide.pt.download_view.domain.model.PayloadMapper
import cm.aptoide.pt.feature_apps.data.App

class PayloadMapperImpl() : PayloadMapper {
  override fun getPayloadFrom(app: App): String? = null
}
