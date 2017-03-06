package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.ListStores;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;

/**
 * Created by trinkes on 12/12/2016.
 */

public class GetMyStoreListRequest extends V7<ListStores, GetMyStoreListRequest.EndlessBody> {

  private static boolean useEndless;
  private final String url;

  public GetMyStoreListRequest(String url, EndlessBody body, String baseHost) {
    super(body, baseHost);
    this.url = url;
  }

  public static GetMyStoreListRequest of(String url, BodyDecorator bodyDecorator) {
    return of(url, false, bodyDecorator);
  }

  public static GetMyStoreListRequest of(String url, boolean useEndless,
      BodyDecorator bodyDecorator) {
    GetMyStoreListRequest.useEndless = useEndless;

    return new GetMyStoreListRequest(url,
        (EndlessBody) bodyDecorator.decorate(new EndlessBody(WidgetsArgs.createDefault())),
        BASE_HOST);
  }

  @Override
  protected Observable<ListStores> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    if (url.contains("getSubscribed")) {
      body.setRefresh(bypassCache);
    }
    if (useEndless) {
      return interfaces.getMyStoreListEndless(url, body, bypassCache);
    } else {
      return interfaces.getMyStoreList(url, body, bypassCache);
    }
  }

  @EqualsAndHashCode(callSuper = true) public static class EndlessBody extends Body
      implements Endless {

    @Getter private Integer limit = 25;
    @Getter @Setter private int offset;

    public EndlessBody(WidgetsArgs widgetsArgs) {
      super(widgetsArgs);
    }
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {
    @Getter private WidgetsArgs widgetsArgs;
    @Getter @Setter private boolean refresh;

    public Body(WidgetsArgs widgetsArgs) {
      super();
      this.widgetsArgs = widgetsArgs;
    }
  }
}
