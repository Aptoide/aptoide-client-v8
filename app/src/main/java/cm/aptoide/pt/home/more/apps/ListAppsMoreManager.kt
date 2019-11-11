package cm.aptoide.pt.home.more.apps

import cm.aptoide.pt.ads.AdsRepository
import cm.aptoide.pt.ads.data.AptoideNativeAd
import cm.aptoide.pt.database.realm.MinimalAd
import cm.aptoide.pt.dataprovider.model.v7.ListApps
import cm.aptoide.pt.dataprovider.model.v7.listapp.App
import cm.aptoide.pt.view.app.Application
import rx.Observable

class ListAppsMoreManager(val listAppsMoreRepository: ListAppsMoreRepository,
                          val adsRepository: AdsRepository) {

  private var offset = 0
  private var total = 0
  private var next = 0

  fun loadFreshApps(url: String?, refresh: Boolean, type: String?): Observable<List<Application>> {
    return if (type.equals("getAds"))
      adsRepository.getAdsFromHomepageMore(refresh).map { response -> mapAdsResponse(response) }
    else
      listAppsMoreRepository.getApps(url, refresh).map { response -> mapResponse(response) }
  }

  fun loadMoreApps(url: String?, refresh: Boolean, type: String?): Observable<List<Application>> {
    return if (type.equals("getAds") || offset >= total)
      Observable.just(null)
    else {
      listAppsMoreRepository.loadMoreApps(url, refresh, next)
          .map { response -> mapResponse(response) }
    }
  }

  private fun mapResponse(listApps: ListApps): List<Application> {
    val result = ArrayList<Application>()
    total = listApps.dataList.total
    offset = listApps.dataList.offset
    next = listApps.dataList.next
    for (app: App in listApps.dataList.list) {
      result.add(Application(app.name, app.icon, app.stats.rating.avg, app.stats.downloads,
          app.packageName,
          app.id, "", app.appcoins != null && app.appcoins.hasBilling()))
    }
    return result
  }

  private fun mapAdsResponse(response: List<MinimalAd>): List<Application>? {
    val result = ArrayList<Application>()
    for (ad: MinimalAd in response) {
      result.add(AptoideNativeAd(ad))
    }
    return result
  }


}