package cm.aptoide.pt.v8engine.timeline.view.displayable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.WindowManager;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.timeline.Article;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import cm.aptoide.pt.v8engine.link.Link;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.view.ShareCardCallback;
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
 * Created by marcelobenites on 6/17/16.
 */
public class ArticleDisplayable extends CardDisplayable {

  public static final String CARD_TYPE_NAME = "ARTICLE";
  @Getter private String cardId;
  @Getter private String articleTitle;
  @Getter private Link link;
  @Getter private Link developerLink;
  @Getter private String title;
  @Getter private String thumbnailUrl;
  @Getter private String avatarUrl;
  @Getter private long appId;

  @Getter private String abUrl;
  @Getter private List<App> relatedToAppsList;

  private Date date;
  private DateCalculator dateCalculator;
  private SpannableFactory spannableFactory;
  private TimelineAnalytics timelineAnalytics;
  private SocialRepository socialRepository;
  private InstalledRepository installedRepository;

  public ArticleDisplayable() {
  }

  public ArticleDisplayable(Article article, String cardId, String articleTitle, Link link,
      Link developerLink, String title, String thumbnailUrl, String avatarUrl, long appId,
      String abUrl, List<App> relatedToAppsList, Date date, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository, InstalledRepository installedRepository,
      WindowManager windowManager) {
    super(article, timelineAnalytics, windowManager);
    this.cardId = cardId;
    this.articleTitle = articleTitle;
    this.link = link;
    this.developerLink = developerLink;
    this.title = title;
    this.thumbnailUrl = thumbnailUrl;
    this.avatarUrl = avatarUrl;
    this.appId = appId;
    this.abUrl = abUrl;
    this.relatedToAppsList = relatedToAppsList;
    this.date = date;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.timelineAnalytics = timelineAnalytics;
    this.socialRepository = socialRepository;
    this.installedRepository = installedRepository;
  }

  public static ArticleDisplayable from(Article article, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, LinksHandlerFactory linksHandlerFactory,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository,
      InstalledRepository installedRepository, WindowManager windowManager) {
    long appId = 0;

    String abTestingURL = null;

    if (article.getAb() != null
        && article.getAb()
        .getConversion() != null
        && article.getAb()
        .getConversion()
        .getUrl() != null) {
      abTestingURL = article.getAb()
          .getConversion()
          .getUrl();
    }

    return new ArticleDisplayable(article, article.getCardId(), article.getTitle(),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, article.getUrl()),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, article.getPublisher()
            .getBaseUrl()), article.getPublisher()
        .getName(), article.getThumbnailUrl(), article.getPublisher()
        .getLogoUrl(), appId, abTestingURL, article.getApps(), article.getDate(), dateCalculator,
        spannableFactory, timelineAnalytics, socialRepository, installedRepository, windowManager);
  }

  public Observable<List<Installed>> getRelatedToApplication() {
    if (relatedToAppsList != null && relatedToAppsList.size() > 0) {
      List<String> packageNamesList = new ArrayList<String>();

      for (int i = 0; i < relatedToAppsList.size(); i++) {
        packageNamesList.add(relatedToAppsList.get(i)
            .getPackageName());
      }

      final String[] packageNames = packageNamesList.toArray(new String[packageNamesList.size()]);

      return installedRepository.getInstalled(packageNames)
          .observeOn(Schedulers.computation());
    }
    return Observable.just(null);
  }

  public String getTimeSinceLastUpdate(Context context) {
    return dateCalculator.getTimeSinceDate(context, date);
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

  public Spannable getStyleText(Context context, String sourceName) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_title_card_title_post_past_singular, sourceName),
        ContextCompat.getColor(context, R.color.black_87_alpha), sourceName);
  }

  public void sendOpenArticleEvent(String packageName) {
    timelineAnalytics.sendOpenArticleEvent(ArticleDisplayable.CARD_TYPE_NAME, getTitle(),
        getLink().getUrl(), packageName);
  }

  public void sendArticleWidgetCardClickEvent(String action, String socialAction) {
    timelineAnalytics.sendMediaCardClickEvent(CARD_TYPE_NAME, getArticleTitle(), getTitle(), action,
        socialAction);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_article;
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
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, BLANK, getTitle(),
            getArticleTitle()));
  }

  @Override public void like(Context context, String cardId, String cardType, int rating,
      Resources resources) {
    socialRepository.like(cardId, cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, BLANK, getTitle(),
            getArticleTitle()));
  }
}
