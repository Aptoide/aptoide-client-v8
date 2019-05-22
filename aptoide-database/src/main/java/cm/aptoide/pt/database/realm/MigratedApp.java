package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;

public class MigratedApp extends RealmObject {
  private String packageName;

  public MigratedApp() {
  }

  public MigratedApp(String packageName) {
    this.packageName = packageName;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }
}
