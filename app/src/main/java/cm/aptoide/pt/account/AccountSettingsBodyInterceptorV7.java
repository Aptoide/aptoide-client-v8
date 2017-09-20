package cm.aptoide.pt.account;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.preferences.AdultContent;
import rx.Single;

public class AccountSettingsBodyInterceptorV7 implements BodyInterceptor<BaseBody> {

  private final BodyInterceptor<BaseBody> bodyInterceptorV7;
  private final AdultContent adultContent;

  public AccountSettingsBodyInterceptorV7(BodyInterceptor<BaseBody> bodyInterceptorV7,
      AdultContent adultContent) {
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.adultContent = adultContent;
  }

  @Override public Single<BaseBody> intercept(BaseBody body) {
    return Single.zip(bodyInterceptorV7.intercept(body), adultContent.enabled()
        .first()
        .toSingle(), (bodyV7, adultContentEnabled) -> {
      bodyV7.setMature(adultContentEnabled);
      return bodyV7;
    });
  }
}
