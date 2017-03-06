package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.BaseV7Response;
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
  private final String accessToken;

  protected ShareInstallCardRequest(Body body, String baseHost, String packageName, String type,
      String accessToken) {
    super(body, baseHost);
    this.packageName = packageName;
    this.type = type;
    this.accessToken = accessToken;
  }

  public static ShareInstallCardRequest of(String packageName, String accessToken, String shareType,
      BodyDecorator bodyDecorator) {
    ShareInstallCardRequest.Body body = new ShareInstallCardRequest.Body(packageName);
    return new ShareInstallCardRequest((ShareInstallCardRequest.Body) bodyDecorator.decorate(body),
        BASE_HOST, packageName, shareType, accessToken);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.shareInstallCard(body, packageName, accessToken, type);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody {

    private String packageName;

    public Body(String packageName) {
      this.packageName = packageName;
    }
  }
}

