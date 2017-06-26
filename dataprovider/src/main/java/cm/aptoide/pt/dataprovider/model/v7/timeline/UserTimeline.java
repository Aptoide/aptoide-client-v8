package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import lombok.Data;

@Data public class UserTimeline {

  private String name;
  private String avatar;
  private Store store;
}
