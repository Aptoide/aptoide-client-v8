package cm.aptoide.pt.v8engine.addressbook.data;

import cm.aptoide.pt.dataprovider.ws.v7.GetFollowersRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.model.v7.store.Store;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

/**
 * Created by jdandrade on 15/02/2017.
 */

public class SyncAddressBookRequest extends V7<GetFollowers, GetFollowersRequest.Body>
    implements ConnectionsServiceApi {

  public SyncAddressBookRequest(GetFollowersRequest.Body body, String baseHost) {
    super(body, baseHost);
  }

  @Override protected Observable<GetFollowers> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    // TODO: 15/02/2017
    return null;
  }

  @Override public void getNewConnections(ConnectionsServiceCallback<List<Contact>> callback) {
    // TODO: 15/02/2017 replace with real call
    callback.onLoaded(getMockedResponse());
  }

  private List<Contact> getMockedResponse() {

    List<Contact> contacts = new ArrayList<>();

    for (int i = 0; i < 8; i++) {
      Contact contact = new Contact();
      Comment.User user = new Comment.User();
      user.setName("João");
      user.setAvatar(
          "http://pool.img.aptoide.com/user/6e8fa2b3ed61fe34c7baad7332807551_avatar.jpg");
      contact.setPerson(user);
      Store store = new Store();
      store.setName("Joao's store");
      store.setAvatar(
          "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg");
      contact.setStore(store);
      contacts.add(contact);
    }
    return contacts;
  }
}
