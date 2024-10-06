package cm.aptoide.pt.dataprovider.model.v7.listapp;

import java.util.List;

public class Urls {
  private List<Url> impression;
  private List<Url> click;
  private List<Url> download;

  public Urls() {

  }

  public List<Url> getImpression() {
    return impression;
  }

  public void setImpression(List<Url> impression) {
    this.impression = impression;
  }

  public List<Url> getDownload() {
    return download;
  }

  public void setDownload(List<Url> download) {
    this.download = download;
  }

  public List<Url> getClick() {
    return click;
  }

  public void setClick(List<Url> click) {
    this.click = click;
  }

  public static class Url {
    private String name;
    private String url;

    public Url() {
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }
  }
}
