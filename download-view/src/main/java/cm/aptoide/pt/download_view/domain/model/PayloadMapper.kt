package cm.aptoide.pt.download_view.domain.model

import cm.aptoide.pt.feature_apps.data.App

interface PayloadMapper {

  fun getPayloadFrom(app: App) : String?
}
