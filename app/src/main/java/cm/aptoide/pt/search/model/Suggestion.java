package cm.aptoide.pt.search.model;

/**
 * Created by franciscocalado on 07/03/18.
 */

public class Suggestion {

  private String name;
  private String icon;

  public Suggestion(String name, String icon) {
    this.name = name;
    this.icon = icon;
  }

  public String getName() {
    return name;
  }

  public String getIcon() {
    return icon;
  }
}
