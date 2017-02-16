package cm.aptoide.pt.v8engine.addressbook.data;

import java.util.List;

/**
 * Created by jdandrade on 15/02/2017.
 */
public interface ConnectionsServiceApi {
  void getNewConnections(ConnectionsServiceCallback<List<Contact>> callback);

  interface ConnectionsServiceCallback<T> {

    void onLoaded(T connections);
  }
}
