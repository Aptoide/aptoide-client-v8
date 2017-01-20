package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.BaseV7Response;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by jdandrade on 24/11/2016.
 */

public class ShareInstallCardRequest extends V7<BaseV7Response, ShareInstallCardRequest.Body> {

  private static String packageName;
  private static String access_token;

  protected ShareInstallCardRequest(ShareInstallCardRequest.Body body, String baseHost) {
    super(body, baseHost);
  }

  public static ShareInstallCardRequest of(String packageName, String accessToken,
      String aptoideClientUUID) {
    ShareInstallCardRequest.packageName = packageName;
    access_token = accessToken;
    ShareInstallCardRequest.Body body = new ShareInstallCardRequest.Body(packageName);
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    return new ShareInstallCardRequest(
        (ShareInstallCardRequest.Body) decorator.decorate(body, accessToken), BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.shareInstallCard(body, packageName, access_token);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true)
  public static class Body extends BaseBody {

    private String packageName;

    public Body(String packageName) {
      this.packageName = packageName;
    }
  }
}

