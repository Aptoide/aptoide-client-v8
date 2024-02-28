package cm.aptoide.pt.home.more.base

import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.view.app.Application
import rx.Observable

interface ListAppsView<T : Application> : View {
  fun showLoading()
  fun showNoNetworkError()
  fun showGenericError()
  fun showApps(apps: List<T>)
  fun addApps(apps: List<T>)

  fun onBottomReached(): Observable<Void>
  fun getItemClickEvents(): Observable<ListAppsClickEvent<T>>
  fun refreshEvents(): Observable<Void>
  fun errorRetryClick(): Observable<Void>

  fun setToolbarInfo(title: String)
  fun showHeader()
  fun headerClicks(): Observable<Void>

  fun setupEskillsView()
}