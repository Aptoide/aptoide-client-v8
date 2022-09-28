package cm.aptoide.pt.app.appsflyer

import retrofit2.Response
import retrofit2.http.GET
import rx.Single

interface AppsFlyerService {
  @GET("com.igg.android.lordsmobile?pid=aptoide_int&af_siteid=aptoide&c=project42&af_click_lookback=7d&af_ad_id=android&af_channel=editorial&af_ad=article&af_c_id=com.igg.android.lordsmobile")
  fun registerImpression(
  ): Single<Response<AppsFlyerResponse>>
}