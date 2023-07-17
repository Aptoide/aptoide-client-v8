package cm.aptoide.pt.feature_home.data

import cm.aptoide.pt.feature_home.domain.Widget

interface WidgetsRepository {

  suspend fun getStoreWidgets(
    context: String? = null,
    bypassCache: Boolean = false,
  ): List<Widget>
}
