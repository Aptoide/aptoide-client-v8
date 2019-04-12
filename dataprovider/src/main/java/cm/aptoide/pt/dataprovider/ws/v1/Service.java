package cm.aptoide.pt.dataprovider.ws.v1;

import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v1.GetPullNotificationsResponse;
import java.util.List;
import java.util.Map;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface Service {

  @GET("notifications/{id}/campaigns")
  Observable<List<GetPullNotificationsResponse>> getPullCampaignNotifications(@Path("id") String id,
      @QueryMap Map<String, String> options,
      @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);
}
