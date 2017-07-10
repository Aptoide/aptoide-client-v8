package cm.aptoide.pt.v8engine.timeline.post;

import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.v8engine.timeline.request.CardPreviewRequest;
import cm.aptoide.pt.v8engine.timeline.request.PostRequest;
import cm.aptoide.pt.v8engine.timeline.request.RelatedAppsRequest;
import cm.aptoide.pt.v8engine.timeline.response.CardPreview;
import cm.aptoide.pt.v8engine.timeline.response.RelatedApp;
import cm.aptoide.pt.v8engine.timeline.response.Response;
import cm.aptoide.pt.v8engine.timeline.response.ResponseList;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Single;

public interface PostWebService {

  String BASE_URI = "https://ws75.aptoide.com/api/7/";

  @POST("user/timeline/card/preview/get") Single<Response<CardPreview>> getCardPreview(
      @Body CardPreviewRequest request);

  @POST("user/timeline/card/apps/get") Single<ResponseList<DataList<RelatedApp>>> getRelatedApps(
      @Body RelatedAppsRequest request);

  @POST("user/timeline/card/set") Single<Void> postInTimeline(@Body PostRequest request);
}
