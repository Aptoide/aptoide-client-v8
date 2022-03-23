package cm.aptoide.pt.dataprovider.ws.v7.home;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionItemData {
  private String type;
  private String id;
  private String icon;
  private String title;
  private String caption;
  private String summary;
  private String url;
  private String views;
  private String date;
  private String flair;
  private Appearance appearance;
  @JsonProperty("package") private String packageName;
  private String graphic;

  public String getType() {
    return type;
  }

  public void setType(String layout) {
    this.type = layout;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getCaption() {
    return caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getViews() {
    return views;
  }

  public void setViews(String views) {
    this.views = views;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Appearance getAppearance() {
    return appearance;
  }

  public void setAppearance(Appearance appearance) {
    this.appearance = appearance;
  }

  public String getFlair() {
    return flair;
  }

  public void setFlair(String flair) {
    this.flair = flair;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getGraphic() {
    return graphic;
  }

  public void setGraphic(String graphic) {
    this.graphic = graphic;
  }
}
