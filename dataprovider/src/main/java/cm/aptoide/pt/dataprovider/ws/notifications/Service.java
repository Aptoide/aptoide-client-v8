package cm.aptoide.pt.dataprovider.ws.notifications;

import cm.aptoide.pt.dataprovider.WebService;
import java.util.List;
import java.util.Map;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface Service {

  @GET("{id}/campaigns")
  Observable<List<GetPullNotificationsResponse>> getPullCompaignNotifications(@Path("id") String id,
      @QueryMap Map<String, String> options,
      @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

  @GET("{id}/direct") Observable<List<GetPullNotificationsResponse>> getPullSocialNotifications(
      @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache, @Path("id") String id,
      @Query("select") List<Integer> types, @QueryMap Map<String, String> options);
}
