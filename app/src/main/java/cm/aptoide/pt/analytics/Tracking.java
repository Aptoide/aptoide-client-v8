package cm.aptoide.pt.analytics;

import lombok.Data;

/**
 * Created by pedroribeiro on 29/06/17.
 */

@Data public class Tracking {

  private final String url;
  private final String packageName;
  private final String country;
  private final String browser;
  private final String siteVersion;
  private final String userAgent;

  public Tracking(String url, String packageName, String country, String browser,
      String siteVersion, String userAgent) {
    this.url = url;
    this.packageName = packageName;
    this.country = country;
    this.browser = browser;
    this.siteVersion = siteVersion;
    this.userAgent = userAgent;
  }
}
