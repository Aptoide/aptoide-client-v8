package cm.aptoide.pt.dataprovider.ws.v7.analyticsbody;

import com.fasterxml.jackson.annotation.JsonProperty;

public class App {
  @JsonProperty("package") private String packageName;
  private String url;
  private String mirror;

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getMirror() {
    return mirror;
  }

  public void setMirror(String mirror) {
    this.mirror = mirror;
  }
}
