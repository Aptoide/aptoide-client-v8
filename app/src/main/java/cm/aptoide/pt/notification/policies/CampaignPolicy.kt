package cm.aptoide.pt.notification.policies

import cm.aptoide.pt.install.InstalledAppsRepository
import cm.aptoide.pt.notification.Policy
import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.Maybe
import rx.Single

class CampaignPolicy(
  private val whitelistedPackages: List<String>,
  private val installedAppsRepository: InstalledAppsRepository
) : Policy {
  override fun shouldShow(): Single<Boolean> {
    if (whitelistedPackages.isEmpty()) {
      return Single.just(true)
    }
    return RxJavaInterop.toV1Single(installedAppsRepository.getInstalledAppsNames().toObservable()
      .flatMapIterable {
        it
      }.flatMapMaybe { installed ->
        getCommonPackages(installed)
      }.toList().flatMap {
        if (it.isEmpty()) {
          return@flatMap io.reactivex.Single.just(false)
        }
        return@flatMap io.reactivex.Single.just(true)
      }
    )
  }

  private fun getCommonPackages(installed: String): Maybe<String> {
    whitelistedPackages.forEach {
      if (installed == it) {
        return Maybe.just(it)
      }
    }
    return Maybe.empty()
  }
}