package cm.aptoide.pt.v8engine.timeline.view.displayable;

import android.content.Context;
import android.content.res.Resources;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.timeline.PopularApp;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.view.ShareCardCallback;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.Date;
import java.util.List;

import static cm.aptoide.pt.v8engine.analytics.Analytics.AppsTimeline.BLANK;

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
  private Long appStoreId;

  private SocialRepository socialRepository;

  public PopularAppDisplayable() {
  }

  public PopularAppDisplayable(PopularApp card, DateCalculator dateCalculator,
      SocialRepository socialRepository, TimelineAnalytics timelineAnalytics,
      WindowManager windowManager) {
    super(card, timelineAnalytics, windowManager);
    this.date = card.getDate();
    this.friends = card.getUsers();
    this.numberOfFriends = card.getUsers()
        .size();
    this.appIcon = card.getPopularApplication()
        .getIcon();
    this.appName = card.getPopularApplication()
        .getName();
    this.appAverageRating = card.getPopularApplication()
        .getStats()
        .getRating()
        .getAvg();
    this.dateCalculator = dateCalculator;
    this.packageName = card.getPopularApplication()
        .getPackageName();
    this.storeName = card.getPopularApplication()
        .getStore()
        .getName();
    this.appId = card.getPopularApplication()
        .getId();
    this.socialRepository = socialRepository;
    this.appStoreId = card.getPopularApplication()
        .getStore()
        .getId();

    if (card.getAb() != null
        && card.getAb()
        .getConversion() != null
        && card.getAb()
        .getConversion()
        .getUrl() != null) {
      this.abUrl = card.getAb()
          .getConversion()
          .getUrl();
    }
  }

  public static Displayable from(PopularApp card, DateCalculator dateCalculator,
      SocialRepository socialRepository, TimelineAnalytics timelineAnalytics,
      WindowManager windowManager) {
    return new PopularAppDisplayable(card, dateCalculator, socialRepository, timelineAnalytics,
        windowManager);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_popular_app;
  }

  @Override
  public void share(String cardId, boolean privacyResult, ShareCardCallback shareCardCallback,
      Resources resources) {
    socialRepository.share(getTimelineCard().getCardId(), getAppStoreId(), privacyResult,
        shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, getPackageName(), BLANK,
            BLANK));
  }

  @Override
  public void share(String cardId, ShareCardCallback shareCardCallback, Resources resources) {
    socialRepository.share(getTimelineCard().getCardId(), getAppStoreId(), shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, getPackageName(), BLANK,
            BLANK));
  }

  @Override public void like(Context context, String cardType, int rating, Resources resources) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, getPackageName(), BLANK, BLANK));
  }

  @Override public void like(Context context, String cardId, String cardType, int rating,
      Resources resources) {
    socialRepository.like(cardId, cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, getPackageName(), BLANK, BLANK));
  }

  public String getCardTitleText(Context context) {
    return context.getString(R.string.timeline_title_popular_app_among_friends);
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

  public Long getAppStoreId() {
    return appStoreId;
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
