package cm.aptoide.pt.timeline;

/**
 * Created by pedroribeiro on 09/05/17.
 */

public class TimelineSocialActionData {

  private final String cardType;
  private final String action;
  private final String socialAction;
  private final String packageName;
  private final String publisher;
  private final String title;

  public TimelineSocialActionData(String cardType, String action, String socialAction,
      String packageName, String publisher, String title) {
    this.cardType = cardType;
    this.action = action;
    this.socialAction = socialAction;
    this.packageName = packageName;
    this.publisher = publisher;
    this.title = title;
  }

  public String getCardType() {
    return cardType;
  }

  public String getAction() {
    return action;
  }

  public String getSocialAction() {
    return socialAction;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getPublisher() {
    return publisher;
  }

  public String getTitle() {
    return title;
  }
}
