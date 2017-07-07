package cm.aptoide.pt.database.realm;

import cm.aptoide.pt.utils.IdUtils;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by trinkes on 5/18/16.
 */
public class RealmString extends RealmObject {

  @PrimaryKey private String id;
  private String string;

  public RealmString(String string) {
    this.id = IdUtils.randomString();
    this.string = string;
  }

  public RealmString() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }
}
