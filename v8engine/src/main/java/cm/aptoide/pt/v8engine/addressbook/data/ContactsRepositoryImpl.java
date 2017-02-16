package cm.aptoide.pt.v8engine.addressbook.data;

import android.support.annotation.NonNull;

/**
 * Created by jdandrade on 15/02/2017.
 */

public class ContactsRepositoryImpl implements ContactsRepository {

  private ConnectionsServiceApi mConnectionsServiceApi;

  public ContactsRepositoryImpl(@NonNull ConnectionsServiceApi contactsServiceApi) {
    this.mConnectionsServiceApi = contactsServiceApi;
  }

  @Override public void getContacts(@NonNull LoadContactsCallback callback) {
    mConnectionsServiceApi.getNewConnections(callback::onContactsLoaded);
  }
}
