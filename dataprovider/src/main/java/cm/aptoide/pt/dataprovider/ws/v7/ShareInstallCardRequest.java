package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by jdandrade on 24/11/2016.
 */

public class ShareInstallCardRequest extends V7<BaseV7Response, ShareInstallCardRequest.Body> {

  private final String packageName;
  private final String type;

  protected ShareInstallCardRequest(Body body, String packageName, String type,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, BASE_HOST,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
    this.packageName = packageName;
    this.type = type;
  }

  public static ShareInstallCardRequest of(String packageName, String shareType,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    ShareInstallCardRequest.Body body = new ShareInstallCardRequest.Body(packageName);
    return new ShareInstallCardRequest(body, packageName, shareType, bodyInterceptor);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.shareInstallCard(body, packageName, body.getAccessToken(), type);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody {

    private String packageName;

    public Body(String packageName) {
      this.packageName = packageName;
    }
  }
}

