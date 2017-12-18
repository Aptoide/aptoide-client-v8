package cm.aptoide.pt.util;

/**
 * Created by neuro on 01-08-2017.
 */
public enum StoreEnum {

  Apps(15);

  private final int id;

  StoreEnum(int id) {
    this.id = id;
  }

  public int getId() {
    return this.id;
  }
}
