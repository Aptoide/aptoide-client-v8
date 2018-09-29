package cm.aptoide.pt.home;

public class ActionItem {
  private final String cardId;
  private final String layout;
  private final String title;
  private final String message;
  private final String icon;
  private final String url;
  private final String type;

  public ActionItem(String cardId, String layout, String title, String message, String icon,
      String url, String type) {
    this.cardId = cardId;
    this.layout = layout;
    this.title = title;
    this.message = message;
    this.icon = icon;
    this.url = url;
    this.type = type;
  }

  public String getCardId() {
    return cardId;
  }

  public String getLayout() {
    return layout;
  }

  public String getTitle() {
    return title;
  }

  public String getMessage() {
    return message;
  }

  public String getIcon() {
    return icon;
  }

  public String getUrl() {
    return url;
  }

  public String getType() {
    return type;
  }
}
