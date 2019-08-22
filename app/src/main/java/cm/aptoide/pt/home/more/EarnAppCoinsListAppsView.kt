package cm.aptoide.pt.home.more

import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.presenter.View

interface EarnAppCoinsListAppsView : View{
  fun showLoading()
  fun showApps(apps: List<RewardApp>)
  fun setToolbarInfo(title: String)
}