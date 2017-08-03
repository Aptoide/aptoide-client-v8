package cm.aptoide.pt.v8engine.timeline.view.displayable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.WindowManager;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialArticle;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import cm.aptoide.pt.v8engine.link.Link;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.view.ShareCardCallback;
import cm.aptoide.pt.v8engine.timeline.view.navigation.AppsTimelineNavigator;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import rx.Observable;
import rx.schedulers.Schedulers;

import static cm.aptoide.pt.v8engine.analytics.Analytics.AppsTimeline.BLANK;

/**
 * Created by jdandrade on 23/11/2016.
 */

public class SocialArticleDisplayable extends SocialCardDisplayable {

  public static final String CARD_TYPE_NAME = "SOCIAL_ARTICLE";
  @Getter private String articleTitle;
  @Getter private Link link;
  @Getter private Link developerLink;
  @Getter private String title;
  @Getter private String thumbnailUrl;
  @Getter private String avatarUrl;
  @Getter private long appId;
  @Getter private String abUrl;
  @Getter private Comment.User user;
  @Getter private String packageName;

  @Getter private List<App> relatedToAppsList;
  private Date date;
  private DateCalculator dateCalculator;
  private SpannableFactory spannableFactory;
  private TimelineAnalytics timelineAnalytics;
  private SocialRepository socialRepository;
  private InstalledRepository installedRepository;

  public SocialArticleDisplayable() {
  }

  public SocialArticleDisplayable(SocialArticle socialArticle, String articleTitle, Link link,
      Link developerLink, String title, String thumbnailUrl, String avatarUrl, long appId,
      String abUrl, String content, Store store, Comment.User user, long numberOfLikes,
      long numberOfComments, List<App> relatedToAppsList, Date date, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository, InstalledRepository installedRepository,
      AppsTimelineNavigator timelineNavigator, WindowManager windowManager) {
    super(socialArticle, numberOfLikes, numberOfComments, store, user,
        socialArticle.getUserSharer(), socialArticle.getMy()
            .isLiked(), socialArticle.getLikes(), socialArticle.getComments(), date,
        spannableFactory, dateCalculator, abUrl, timelineAnalytics, timelineNavigator,
        windowManager);
    this.articleTitle = articleTitle;
    this.link = link;
    this.developerLink = developerLink;
    this.title = title;
    this.thumbnailUrl = thumbnailUrl;
    this.avatarUrl = avatarUrl;
    this.appId = appId;
    this.abUrl = abUrl;
    this.user = user;
    this.relatedToAppsList = relatedToAppsList;
    this.date = date;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.timelineAnalytics = timelineAnalytics;
    this.socialRepository = socialRepository;
    this.installedRepository = installedRepository;
  }

  public static SocialArticleDisplayable from(SocialArticle socialArticle,
      DateCalculator dateCalculator, SpannableFactory spannableFactory,
      LinksHandlerFactory linksHandlerFactory, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository, InstalledRepository installedRepository,
      AppsTimelineNavigator timelineNavigator, WindowManager windowManager) {
    long appId = 0;

    String abTestingURL = null;

    if (socialArticle.getAb() != null
        && socialArticle.getAb()
        .getConversion() != null
        && socialArticle.getAb()
        .getConversion()
        .getUrl() != null) {
      abTestingURL = socialArticle.getAb()
          .getConversion()
          .getUrl();
    }

    return new SocialArticleDisplayable(socialArticle, socialArticle.getTitle(),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, socialArticle.getUrl()),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            socialArticle.getPublisher()
                .getBaseUrl()), socialArticle.getPublisher()
        .getName(), socialArticle.getThumbnailUrl(), socialArticle.getPublisher()
        .getLogoUrl(), appId, abTestingURL, socialArticle.getContent(), socialArticle.getStore(),
        socialArticle.getUser(), socialArticle.getStats()
        .getLikes(), socialArticle.getStats()
        .getComments(), socialArticle.getApps(), socialArticle.getDate(), dateCalculator,
        spannableFactory, timelineAnalytics, socialRepository, installedRepository,
        timelineNavigator, windowManager);
  }

  public Observable<List<Installed>> getRelatedToApplication() {
    if (relatedToAppsList != null && relatedToAppsList.size() > 0) {
      List<String> packageNamesList = new ArrayList<>();

      for (int i = 0; i < relatedToAppsList.size(); i++) {
        packageNamesList.add(relatedToAppsList.get(i)
            .getPackageName());
      }

      final String[] packageNames = packageNamesList.toArray(new String[packageNamesList.size()]);

      return installedRepository.getInstalled(packageNames)
          .observeOn(Schedulers.computation());
      //appId = video.getApps().get(0).getId();
    }
    return Observable.just(null);
  }

  //public String getTimeSinceLastUpdate(Context context) {
  //  return dateCalculator.getTimeSinceDate(context, date);
  //}

  public boolean isGetApp(String appName) {
    return appName != null && appId != 0;
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
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_title_card_title_share_past_singular, title),
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

  public void sendSocialArticleClickEvent(String action, String socialAction) {
    timelineAnalytics.sendSocialArticleClickEvent(CARD_TYPE_NAME, getArticleTitle(), getTitle(),
        action, socialAction);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_social_article;
  }

  @Override
  public void share(String cardId, boolean privacyResult, ShareCardCallback shareCardCallback,
      Resources resources) {
    socialRepository.share(getTimelineCard().getCardId(), privacyResult, shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, BLANK, getTitle(),
            getArticleTitle()));
  }

  @Override
  public void share(String cardId, ShareCardCallback shareCardCallback, Resources resources) {
    socialRepository.share(getTimelineCard().getCardId(), shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, BLANK, getTitle(),
            getArticleTitle()));
  }

  @Override public void like(Context context, String cardType, int rating, Resources resources) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, BLANK, getTitle(), BLANK));
  }

  @Override public void like(Context context, String cardId, String cardType, int rating,
      Resources resources) {
    socialRepository.like(cardId, cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, BLANK, getTitle(), BLANK));
  }
}
