package cm.aptoide.pt.home.more

import cm.aptoide.pt.presenter.View

interface ListAppsView<in V> : View {
  fun showLoading()
  fun setToolbarInfo(title: String)
  fun showApps(apps: List<V>)
}