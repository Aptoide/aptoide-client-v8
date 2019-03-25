package cm.aptoide.pt.editorialList;

public class CurationCard {
  private final String id;
  private final String subTitle;
  private final String icon;
  private final String title;
  private final String views;
  private final String type;

  public CurationCard(String id, String subTitle, String icon, String title, String views,
      String type) {
    this.id = id;
    this.subTitle = subTitle;
    this.icon = icon;
    this.title = title;
    this.views = views;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public String getSubTitle() {
    return subTitle;
  }

  public String getIcon() {
    return icon;
  }

  public String getTitle() {
    return title;
  }

  public String getViews() {
    return views;
  }

  public String getType() {
    return type;
  }
}
