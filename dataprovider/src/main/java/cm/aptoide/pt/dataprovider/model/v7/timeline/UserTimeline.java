package cm.aptoide.pt.model.v7.timeline;

import cm.aptoide.pt.model.v7.store.Store;
import lombok.Data;

@Data public class UserTimeline {

  private String name;
  private String avatar;
  private Store store;
}
