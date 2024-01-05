package cm.aptoide.pt.home.more.eskills

import cm.aptoide.pt.home.more.base.ListAppsView
import cm.aptoide.pt.view.app.Application
import rx.Observable

interface EskillsInfoView: ListAppsView<Application> {
  fun handleMoreAppsClick(): Observable<Void>
  fun handleLearnMoreClick(): Observable<Void>
  fun handleWalletDisclaimerClick(): Observable<Void>
  fun scrollToInfo()
}