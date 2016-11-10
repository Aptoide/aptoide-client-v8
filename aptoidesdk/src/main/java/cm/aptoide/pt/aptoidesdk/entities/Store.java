package cm.aptoide.pt.aptoidesdk.entities;

import lombok.Data;

/**
 * Created by neuro on 03-11-2016.
 */
@Data public class Store {
  private final long id;
  private final String name;
  private final String avatarPath;

  public static Store from(cm.aptoide.pt.model.v7.store.Store store) {

    long id = store.getId();
    String name = store.getName();
    String avatarPath = store.getAvatar();

    return new Store(id, name, avatarPath);
  }
}
