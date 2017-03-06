package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.model.v7.BaseV7Response;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by pedroribeiro on 16/12/16.
 */

public class SetConnectionRequest extends V7<BaseV7Response, SetConnectionRequest.Body> {

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";

  protected SetConnectionRequest(Body body, String baseHost) {
    super(body, baseHost);
  }

  public static SetConnectionRequest of(String userPhone, BodyDecorator bodyDecorator) {
    Body body = new Body(userPhone);
    return new SetConnectionRequest((Body) bodyDecorator.decorate(body), BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setConnection(body);
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {

    private String phoneHash;

    public Body(String userPhone) {
      this.phoneHash = userPhone;
    }
  }
}
