package cm.aptoide.pt.v8engine.timeline.post;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.v8engine.timeline.request.RelatedAppsRequest;
import rx.Single;

class PostRequestBuilder {

  private BodyInterceptor<BaseBody> baseBodyInterceptor;

  PostRequestBuilder(BodyInterceptor<BaseBody> baseBodyInterceptor) {
    this.baseBodyInterceptor = baseBodyInterceptor;
  }

  public Single<RelatedAppsRequest> getRelatedAppsRequest(String url) {
    return Single.just(new RelatedAppsRequest(url))
        .flatMap(relatedAppsRequest -> baseBodyInterceptor.intercept(relatedAppsRequest)
            .map(baseBody -> ((RelatedAppsRequest) baseBody)));
  }
}
