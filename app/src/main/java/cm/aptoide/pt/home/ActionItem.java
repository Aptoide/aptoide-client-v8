package cm.aptoide.pt.home;

public class ActionItem {
  private final String cardId;
  private final String type;
  private final String title;
  private final String subTitle;
  private final String icon;
  private final String url;
  private final String numberOfViews;

  public ActionItem(String cardId, String type, String title, String subTitle, String icon,
      String url, String numberOfViews) {
    this.cardId = cardId;
    this.type = type;
    this.title = title;
    this.subTitle = subTitle;
    this.icon = icon;
    this.url = url;
    this.numberOfViews = numberOfViews;
  }

  public String getCardId() {
    return cardId;
  }

  public String getType() {
    return type;
  }

  public String getTitle() {
    return title;
  }

  public String getSubTitle() {
    return subTitle;
  }

  public String getIcon() {
    return icon;
  }

  public String getUrl() {
    return url;
  }

  public String getNumberOfViews() {
    return numberOfViews;
  }
}
