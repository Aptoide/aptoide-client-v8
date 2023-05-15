package cm.aptoide.pt.feature_home.data

import cm.aptoide.pt.feature_home.domain.Widget

interface WidgetsRepository {

  suspend fun getStoreWidgets(bypassCache: Boolean = false): List<Widget>
  suspend fun getActionUrl(tag: String): String?

}
