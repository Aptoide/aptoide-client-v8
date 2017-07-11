package cm.aptoide.pt.v8engine.timeline.post;

import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.v8engine.timeline.request.RelatedAppsRequest;
import cm.aptoide.pt.v8engine.timeline.response.RelatedApp;
import cm.aptoide.pt.v8engine.timeline.response.ResponseList;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Single;

public interface PostWebService {

  String BASE_URI = "https://ws75.aptoide.com/api/7/";

  @POST("user/timeline/card/apps/get") Single<ResponseList<DataList<RelatedApp>>> getRelatedApps(
      @Body RelatedAppsRequest request);

}
