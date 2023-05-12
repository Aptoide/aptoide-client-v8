package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App

interface AppsListUseCase {
  suspend fun getAppsList(source: String): List<App>
}
