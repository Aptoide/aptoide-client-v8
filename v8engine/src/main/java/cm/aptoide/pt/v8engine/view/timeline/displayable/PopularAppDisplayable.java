package cm.aptoide.pt.v8engine.view.timeline.displayable;

import android.content.Context;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.timeline.PopularApp;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.ShareCardCallback;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 27/04/2017.
 */

public class PopularAppDisplayable extends CardDisplayable {
  public static final String CARD_TYPE_NAME = "POPULAR_APP";
  private Date date;
  private List<Comment.User> friends;
  private int numberOfFriends;
  private String appIcon;
  private String appName;
  private float appAverageRating;
  private DateCalculator dateCalculator;

  private String abUrl;
  private long appId;
  private String packageName;
  private String storeName;

  public PopularAppDisplayable() {
  }

  public PopularAppDisplayable(PopularApp card, DateCalculator dateCalculator) {
    this.date = card.getDate();
    this.friends = card.getUsers();
    this.numberOfFriends = card.getUsers().size();
    this.appIcon = card.getPopularApplication().getIcon();
    this.appName = card.getPopularApplication().getName();
    this.appAverageRating = card.getPopularApplication().getStats().getRating().getAvg();
    this.dateCalculator = dateCalculator;
    this.packageName = card.getPopularApplication().getPackageName();
    this.storeName = card.getPopularApplication().getStore().getName();
    this.appId = card.getPopularApplication().getId();

    if (card.getAb() != null
        && card.getAb().getConversion() != null
        && card.getAb().getConversion().getUrl() != null) {
      this.abUrl = card.getAb().getConversion().getUrl();
    }
  }

  public static Displayable from(PopularApp card, DateCalculator dateCalculator) {
    return new PopularAppDisplayable(card, dateCalculator);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_popular_app;
  }

  @Override
  public void share(Context context, boolean privacyResult, ShareCardCallback shareCardCallback) {

  }

  @Override public void share(Context context, ShareCardCallback shareCardCallback) {

  }

  @Override public void like(Context context, String cardType, int rating) {

  }

  @Override public void like(Context context, String cardId, String cardType, int rating) {

  }

  public String getCardTitleText(Context context) {
    return context.getString(R.string.popular_app_among_friends);
  }

  public String getTimeSinceLastUpdate(Context context) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  public List<Comment.User> getFriends() {
    return friends;
  }

  public int getNumberOfFriends() {
    return numberOfFriends;
  }

  public String getAppIcon() {
    return appIcon;
  }

  public String getAppName() {
    return appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getAbUrl() {
    return abUrl;
  }

  public String getStoreName() {
    return storeName;
  }

  public long getAppId() {
    return appId;
  }

  public float getAppAverageRating() {
    return appAverageRating;
  }

  public Date getDate() {
    return date;
  }
}
