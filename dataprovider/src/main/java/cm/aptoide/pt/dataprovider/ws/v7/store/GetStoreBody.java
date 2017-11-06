package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;

/**
 * Created by trinkes on 23/02/2017.
 */
public class GetStoreBody extends BaseBodyWithStore {

  private final WidgetsArgs widgetsArgs;
  private StoreContext context;

  public GetStoreBody(BaseRequestWithStore.StoreCredentials storeCredentials,
      WidgetsArgs widgetsArgs) {
    super(storeCredentials);
    this.widgetsArgs = widgetsArgs;
  }

  public StoreContext getContext() {
    return context;
  }

  public void setContext(StoreContext context) {
    this.context = context;
  }

  public WidgetsArgs getWidgetsArgs() {
    return widgetsArgs;
  }
}
