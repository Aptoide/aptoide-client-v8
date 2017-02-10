package cm.aptoide.pt.database.realm;

import cm.aptoide.pt.utils.IdUtils;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by trinkes on 5/18/16.
 */
public class RealmInteger extends RealmObject {

  @PrimaryKey private String id;
  private Integer integer;

  public RealmInteger(Integer downloadId) {
    this.id = IdUtils.randomString();
    this.integer = downloadId;
  }

  public RealmInteger() {
  }

  public Integer getInteger() {
    return integer;
  }

  public void setInteger(Integer integer) {
    this.integer = integer;
  }
}
