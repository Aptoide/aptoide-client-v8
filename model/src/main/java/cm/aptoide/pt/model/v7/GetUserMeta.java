package cm.aptoide.pt.model.v7;

import cm.aptoide.pt.model.v7.store.Store;
import lombok.Data;

/**
 * Created by pedroribeiro on 29/05/17.
 */

@Data public class GetUserMeta extends BaseV7Response {

  private Data data;

  @lombok.Data public static class Data {
    private long id;
    private String name;
    private int level;
    private String avatar;
    private String added;
    private String modified;
    private Identity identity;
    private Store store;
    private String access;
  }

  @lombok.Data public static class Identity {
    private String username;
    private String email;
    private String phone;
  }
}
