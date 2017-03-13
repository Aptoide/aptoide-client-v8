package cm.aptoide.pt.dataprovider.ws.v7;

import rx.Single;

/**
 * Created by marcelobenites on 06/03/17.
 */

public interface BodyInterceptor {

  public Single<AccessTokenBody> intercept(BaseBody baseBody);
}
