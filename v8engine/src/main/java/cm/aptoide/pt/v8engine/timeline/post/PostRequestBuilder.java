package cm.aptoide.pt.v8engine.timeline.post;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.v8engine.timeline.request.CardPreviewRequest;
import cm.aptoide.pt.v8engine.timeline.request.PostRequest;
import cm.aptoide.pt.v8engine.timeline.request.RelatedAppsRequest;
import rx.Observable;
import rx.Single;

class PostRequestBuilder {

  private BodyInterceptor<BaseBody> baseBodyInterceptor;

  PostRequestBuilder(BodyInterceptor<BaseBody> baseBodyInterceptor) {
    this.baseBodyInterceptor = baseBodyInterceptor;
  }

  public Single<PostRequest> getPostOnTimelineRequest(String url, String content,
      String packageName) {
    return Observable.just(new PostRequest(url, content, packageName))
        .flatMapSingle(postRequest -> baseBodyInterceptor.intercept(postRequest)
            .map(baseBody -> ((PostRequest) baseBody)))
        .toSingle();
  }

  public RelatedAppsRequest getRelatedAppsRequest(String url) {
    return null;
  }

  public CardPreviewRequest getCardPreviewRequest(String url) {
    return null;
  }
}
