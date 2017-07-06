package cm.aptoide.pt.v8engine.timeline.model;

import com.google.gson.annotations.SerializedName;

public class RelatedApp {
  private long id;
  private String name;
  @SerializedName("package") private String packageName;
  private String icon;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }
}
