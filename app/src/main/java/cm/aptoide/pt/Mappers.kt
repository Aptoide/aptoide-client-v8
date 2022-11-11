package cm.aptoide.pt

import cm.aptoide.pt.download_view.domain.model.AppDetailsMapper
import cm.aptoide.pt.feature_apps.data.App

class VanillaAppDetailsMapper : AppDetailsMapper<String> {
  override fun toDetails(app: App) = app.name
}
