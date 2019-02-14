package cm.aptoide.pt.search;

public class SearchHostProvider {
  private final boolean isToolboxEnableHttpScheme;
  private final String aptoideWebServicesScheme;
  private final String aptoideWebServicesSearchHost;
  private final String aptoideWebServicesSearchSslHost;

  public SearchHostProvider(boolean isToolboxEnableHttpScheme, String aptoideWebServicesScheme,
      String aptoideWebServicesSearchHost, String aptoideWebServicesSearchSslHost) {
    this.isToolboxEnableHttpScheme = isToolboxEnableHttpScheme;
    this.aptoideWebServicesScheme = aptoideWebServicesScheme;
    this.aptoideWebServicesSearchHost = aptoideWebServicesSearchHost;
    this.aptoideWebServicesSearchSslHost = aptoideWebServicesSearchSslHost;
  }

  public String getSearchHost() {
    String scheme = isToolboxEnableHttpScheme ? "http" : aptoideWebServicesScheme;
    return "http".equals(scheme) ? scheme + "://" + aptoideWebServicesSearchHost + "/v1/"
        : scheme + "://" + aptoideWebServicesSearchSslHost + "/v1/";
  }
}
