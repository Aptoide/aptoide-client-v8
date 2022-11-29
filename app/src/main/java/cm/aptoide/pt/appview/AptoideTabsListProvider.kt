package cm.aptoide.pt.appview

import cm.aptoide.pt.feature_appview.presentation.AppViewTab
import cm.aptoide.pt.feature_appview.presentation.TabsListProvider
import dagger.hilt.android.scopes.ViewModelScoped

@ViewModelScoped
class AptoideTabsListProvider : TabsListProvider {
  override fun getTabsList(): List<Pair<AppViewTab, Int>> =
    listOf(
      Pair(AppViewTab.DETAILS, 0),
      Pair(AppViewTab.REVIEWS, 1),
      Pair(AppViewTab.RELATED, 2),
      Pair(AppViewTab.VERSIONS, 3),
      Pair(AppViewTab.INFO, 4)
    )
}
