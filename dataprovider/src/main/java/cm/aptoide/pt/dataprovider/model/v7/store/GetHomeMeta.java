package cm.aptoide.pt.dataprovider.model.v7.store;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by trinkes on 23/02/2017.
 */
@Data @EqualsAndHashCode(callSuper = true) public class GetHomeMeta extends BaseV7Response {
  Data data;

  @lombok.Data public static class Data {
    Store store;
    HomeUser user;
    Stats stats;
  }

  @lombok.Data public static class Stats {
    long followers;
    long following;
  }
}
