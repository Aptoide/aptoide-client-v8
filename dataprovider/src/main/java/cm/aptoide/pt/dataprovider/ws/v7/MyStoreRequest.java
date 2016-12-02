package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.MyStore;
import rx.Observable;

/**
 * Created by trinkes on 11/30/16.
 */

public class MyStoreRequest extends V7<MyStore, BaseBody> {
  protected MyStoreRequest(BaseBody body, String baseHost) {
    super(body, baseHost);
  }

  public static MyStoreRequest of() {
    return new MyStoreRequest(new BaseBody(), BASE_HOST);
  }

  @Override
  protected Observable<MyStore> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getMyStore(bypassCache);
  }
}
