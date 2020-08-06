package cm.aptoide.pt

import rx.Completable

class UpdateLaunchManager(val followedStoresManager: FollowedStoresManager) {

  fun runUpdateLaunch(previousVersionCode: Int, currentVersionCode: Int): Completable {
    var completable = Completable.complete()

    // 9.13.3.1
    if (previousVersionCode < 10013) {
      completable = completable.andThen(followedStoresManager.setDefaultFollowedStores())
    }

    return completable
  }
}