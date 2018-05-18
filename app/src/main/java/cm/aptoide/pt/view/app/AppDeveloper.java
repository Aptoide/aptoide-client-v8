package cm.aptoide.pt.view.app;

/**
 * Created by D01 on 17/05/2018.
 */

public class AppDeveloper {
  private final String name;
  private final String email;
  private final String privacy;
  private final String website;

  public AppDeveloper(String name, String email, String privacy, String website) {
    this.name = name;
    this.email = email;
    this.privacy = privacy;
    this.website = website;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getPrivacy() {
    return privacy;
  }

  public String getWebsite() {
    return website;
  }
}
