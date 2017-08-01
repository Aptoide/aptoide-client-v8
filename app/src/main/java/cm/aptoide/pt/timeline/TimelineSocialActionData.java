package cm.aptoide.pt.timeline;

import lombok.Getter;

/**
 * Created by pedroribeiro on 09/05/17.
 */

public class TimelineSocialActionData {

  @Getter private String cardType;
  @Getter private String action;
  @Getter private String socialAction;
  @Getter private String packageName;
  @Getter private String publisher;
  @Getter private String title;

  public TimelineSocialActionData(String cardType, String action, String socialAction,
      String packageName, String publisher, String title) {
    this.cardType = cardType;
    this.action = action;
    this.socialAction = socialAction;
    this.packageName = packageName;
    this.publisher = publisher;
    this.title = title;
  }
}
