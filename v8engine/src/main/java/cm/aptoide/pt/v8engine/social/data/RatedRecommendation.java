package cm.aptoide.pt.v8engine.social.data;

import java.util.Date;

/**
 * Created by jdandrade on 26/06/2017.
 */

public class RatedRecommendation extends AppPost {
  private final float ratingAverage;
  private int titleStringResourceId;

  public RatedRecommendation(String cardId, long appId, String packageName, String appName,
      String appIcon, float ratingAverage, Date timestamp, String abUrl, int titleStringResourceId,
      CardType cardType) {
    super(cardId, appIcon, appName, appId, packageName, timestamp, abUrl, cardType);
    this.ratingAverage = ratingAverage;
    this.titleStringResourceId = titleStringResourceId;
  }

  public float getRatingAverage() {
    return ratingAverage;
  }

  public int getTitleStringResourceId() {
    return titleStringResourceId;
  }
}
