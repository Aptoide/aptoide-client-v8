package cm.aptoide.pt.v8engine.view.timeline.displayable;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.model.v7.timeline.AggregatedSocialArticle;
import cm.aptoide.pt.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.model.v7.timeline.UserSharerTimeline;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.link.Link;
import cm.aptoide.pt.v8engine.timeline.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.timeline.ShareCardCallback;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 17/05/2017.
 */

public class AggregatedSocialArticleDisplayable extends CardDisplayable {
  public static final String CARD_TYPE_NAME = "AGGREGATED_SOCIAL_ARTICLE";
  private Link link;
  private Link developerLink;
  private String title;
  private String thumbnailUrl;
  private String avatarUrl;
  private String abTestingURL;
  private Comment.User user;
  private String packageName;
  private List<App> relatedToAppsList;
  private Date date;
  private DateCalculator dateCalculator;
  private SpannableFactory spannableFactory;
  private TimelineAnalytics timelineAnalytics;
  private SocialRepository socialRepository;
  private List<MinimalCard> minimalCardList;
  private List<UserSharerTimeline> sharers;

  public AggregatedSocialArticleDisplayable() {
  }

  public AggregatedSocialArticleDisplayable(AggregatedSocialArticle card, String title, Link link,
      Link developerLink, String name, String thumbnailUrl, String avatarUrl, String abTestingURL,
      Store store, Comment.User user, List<App> relatedToAppsList, Date date,
      DateCalculator dateCalculator, SpannableFactory spannableFactory,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository) {
    super(card);
    this.link = link;
    this.developerLink = developerLink;
    this.title = title;
    this.thumbnailUrl = thumbnailUrl;
    this.avatarUrl = avatarUrl;
    this.abTestingURL = abTestingURL;
    this.user = user;
    this.relatedToAppsList = relatedToAppsList;
    this.date = date;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.timelineAnalytics = timelineAnalytics;
    this.socialRepository = socialRepository;
    this.minimalCardList = card.getMinimalCardList();
    this.sharers = card.getSharers();
  }

  public static Displayable from(AggregatedSocialArticle card, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, LinksHandlerFactory linksHandlerFactory,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository) {
    String abTestingURL = null;

    if (card.getAb() != null
        && card.getAb()
        .getConversion() != null
        && card.getAb()
        .getConversion()
        .getUrl() != null) {
      abTestingURL = card.getAb()
          .getConversion()
          .getUrl();
    }
    return new AggregatedSocialArticleDisplayable(card, card.getTitle(),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, card.getUrl()),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, card.getPublisher()
            .getBaseUrl()), card.getPublisher()
        .getName(), card.getThumbnailUrl(), card.getPublisher()
        .getLogoUrl(), abTestingURL, card.getStore(), card.getUser(), card.getApps(),
        card.getDate(), dateCalculator, spannableFactory, timelineAnalytics, socialRepository);
  }

  public Observable<List<Installed>> getRelatedToApplication() {
    if (relatedToAppsList != null && relatedToAppsList.size() > 0) {
      InstalledAccessor installedAccessor = AccessorFactory.getAccessorFor(Installed.class);
      List<String> packageNamesList = new ArrayList<>();

      for (int i = 0; i < relatedToAppsList.size(); i++) {
        packageNamesList.add(relatedToAppsList.get(i)
            .getPackageName());
      }

      final String[] packageNames = packageNamesList.toArray(new String[packageNamesList.size()]);

      return installedAccessor.get(packageNames)
          .observeOn(Schedulers.computation());
    }
    return Observable.just(null);
  }

  public Spannable getAppText(Context context, String appName) {
    return spannableFactory.createStyleSpan(
        context.getString(R.string.displayable_social_timeline_article_get_app_button, appName),
        Typeface.BOLD, appName);
  }

  public Spannable getAppRelatedToText(Context context, String appName) {
    return spannableFactory.createStyleSpan(
        context.getString(R.string.displayable_social_timeline_article_related_to, appName),
        Typeface.BOLD, appName);
  }

  public Spannable getStyledTitle(Context context, String title) {
    return spannableFactory.createColorSpan(context.getString(R.string.x_shared, title),
        ContextCompat.getColor(context, R.color.black_87_alpha), title);
  }

  public void sendOpenBlogEvent() {
    timelineAnalytics.sendOpenBlogEvent(CARD_TYPE_NAME, getTitle(), getDeveloperLink().getUrl(),
        packageName);
  }

  public void sendOpenArticleEvent() {
    timelineAnalytics.sendOpenArticleEvent(CARD_TYPE_NAME, getTitle(), getLink().getUrl(),
        packageName);
  }

  public Link getLink() {
    return link;
  }

  public Link getDeveloperLink() {
    return developerLink;
  }

  public String getTitle() {
    return title;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public String getAbTestingURL() {
    return abTestingURL;
  }

  public Comment.User getUser() {
    return user;
  }

  public String getPackageName() {
    return packageName;
  }

  public List<App> getRelatedToAppsList() {
    return relatedToAppsList;
  }

  public Date getDate() {
    return date;
  }

  public List<MinimalCard> getMinimalCardList() {
    return minimalCardList;
  }

  public List<UserSharerTimeline> getSharers() {
    return sharers;
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_aggregated_social_article;
  }

  @Override
  public void share(String cardId, boolean privacyResult, ShareCardCallback shareCardCallback) {
    socialRepository.share(getTimelineCard().getCardId(), privacyResult, shareCardCallback);
  }

  @Override public void share(String cardId, ShareCardCallback shareCardCallback) {
    socialRepository.share(getTimelineCard().getCardId(), shareCardCallback);
  }

  @Override public void like(Context context, String cardType, int rating) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating);
  }

  @Override public void like(Context context, String cardId, String cardType, int rating) {
    socialRepository.like(cardId, cardType, "", rating);
  }

  public String getCardHeaderNames() {
    StringBuilder cardHeaderNames = new StringBuilder();
    for (UserSharerTimeline user : getSharers()) {
      cardHeaderNames.append(user.getStore()
          .getName())
          .append(" ");
    }
    return cardHeaderNames.toString();
  }

  public String getTimeSinceLastUpdate(Context context) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  public String getTimeSinceLastUpdate(Context context, Date date) {
    return dateCalculator.getTimeSinceDate(context, date);
  }
}
