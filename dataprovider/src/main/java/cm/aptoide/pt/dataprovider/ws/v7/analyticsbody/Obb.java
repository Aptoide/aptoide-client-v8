package cm.aptoide.pt.dataprovider.ws.v7.analyticsbody;

public class Obb {
  private ObbType type;
  private String url;
  private String mirror;

  public ObbType getType() {
    return type;
  }

  public void setType(ObbType type) {
    this.type = type;
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

  public enum ObbType {
    MAIN, PATCH
  }
}
