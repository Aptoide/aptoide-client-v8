package cm.aptoide.pt.home.more.appcoins

import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.home.more.ListAppsEvent
import cm.aptoide.pt.home.more.ListAppsView
import cm.aptoide.pt.presenter.View
import rx.Observable

interface EarnAppCoinsListAppsView : ListAppsView<RewardApp> {
  fun appClicked(): Observable<ListAppsEvent<RewardApp>>
}