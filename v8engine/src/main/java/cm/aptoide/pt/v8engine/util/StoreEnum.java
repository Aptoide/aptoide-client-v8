package cm.aptoide.pt.v8engine.util;

import lombok.Getter;

/**
 * Created by neuro on 01-08-2017.
 */
public enum StoreEnum {

  Apps(15);

  @Getter private final int id;

  StoreEnum(int id) {
    this.id = id;
  }
}
