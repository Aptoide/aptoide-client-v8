package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.GetFollowers;
import java.util.List;
import lombok.Data;
import rx.Observable;

/**
 * Created by jdandrade on 15/02/2017.
 */

public class SyncAddressBookRequest extends V7<GetFollowers, SyncAddressBookRequest.Body> {

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "s://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";

  public SyncAddressBookRequest(Body body, String baseHost) {
    super(body, baseHost);
  }

  public static SyncAddressBookRequest of(String accessToken, String aptoideClientUUID,
      List<String> numbers, List<String> emails) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    return new SyncAddressBookRequest(
        ((SyncAddressBookRequest.Body) decorator.decorate(new Body(new Contacts(numbers, emails)),
            accessToken)), BASE_HOST);
  }

  @Override protected Observable<GetFollowers> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setConnections(body);
  }

  //private List<Contact> getMockedResponse() {
  //
  //  List<Contact> contacts = new ArrayList<>();
  //
  //  for (int i = 0; i < 8; i++) {
  //    Contact contact = new Contact();
  //    Comment.User user = new Comment.User();
  //    user.setName("João");
  //    user.setAvatar(
  //        "http://pool.img.aptoide.com/user/6e8fa2b3ed61fe34c7baad7332807551_avatar.jpg");
  //    contact.setPerson(user);
  //    Store store = new Store();
  //    store.setName("Joao's store");
  //    store.setAvatar(
  //        "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg");
  //    contact.setStore(store);
  //    contacts.add(contact);
  //  }
  //  return contacts;
  //}

  @Data public static class Body extends BaseBody implements Endless {
    private Contacts contacts;
    private int limit = 25;
    private int offset;

    public Body(Contacts contacts) {
      this.contacts = contacts;
    }

    @Override public int getOffset() {
      return offset;
    }

    @Override public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override public Integer getLimit() {
      return limit;
    }
  }

  @Data private static class Contacts {
    private List<String> phones;
    private List<String> emails;

    public Contacts(List<String> phones, List<String> emails) {
      this.phones = phones;
      this.emails = emails;
    }
  }
}
