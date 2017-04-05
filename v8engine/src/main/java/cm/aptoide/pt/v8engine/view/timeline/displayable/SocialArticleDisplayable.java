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
import cm.aptoide.pt.model.v7.timeline.SocialArticle;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.ShareCardCallback;
import cm.aptoide.pt.v8engine.timeline.link.Link;
import cm.aptoide.pt.v8engine.timeline.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.repository.TimelineAnalytics;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import rx.Observable;
import rx.schedulers.Schedulers;

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

  public SocialArticleDisplayable() {
  }

  public SocialArticleDisplayable(SocialArticle socialArticle, String articleTitle, Link link,
      Link developerLink, String title, String thumbnailUrl, String avatarUrl, long appId,
      String abUrl, Store store, Comment.User user, long numberOfLikes, long numberOfComments,
      List<App> relatedToAppsList, Date date, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository) {
    super(socialArticle, numberOfLikes, numberOfComments, store, user,
        socialArticle.getUserSharer(), socialArticle.getMy().isLiked(), socialArticle.getLikes(),
        date, spannableFactory, dateCalculator, abUrl);
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
  }

  public static SocialArticleDisplayable from(SocialArticle socialArticle,
      DateCalculator dateCalculator, SpannableFactory spannableFactory,
      LinksHandlerFactory linksHandlerFactory, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository) {
    long appId = 0;
    //if (article.getApps() != null && article.getApps().size() > 0) {
    //  appName = article.getApps().get(0).getName();
    //  appId = article.getApps().get(0).getId();
    //}

    String abTestingURL = null;

    if (socialArticle.getAb() != null
        && socialArticle.getAb().getConversion() != null
        && socialArticle.getAb().getConversion().getUrl() != null) {
      abTestingURL = socialArticle.getAb().getConversion().getUrl();
    }

    return new SocialArticleDisplayable(socialArticle, socialArticle.getTitle(),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, socialArticle.getUrl()),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            socialArticle.getPublisher().getBaseUrl()), socialArticle.getPublisher().getName(),
        socialArticle.getThumbnailUrl(), socialArticle.getPublisher().getLogoUrl(), appId,
        abTestingURL, socialArticle.getStore(), socialArticle.getUser(),
        socialArticle.getStats().getLikes(), socialArticle.getStats().getComments(),
        socialArticle.getApps(), socialArticle.getDate(), dateCalculator, spannableFactory,
        timelineAnalytics, socialRepository);
  }

  public Observable<List<Installed>> getRelatedToApplication() {
    if (relatedToAppsList != null && relatedToAppsList.size() > 0) {
      InstalledAccessor installedAccessor = AccessorFactory.getAccessorFor(Installed.class);
      List<String> packageNamesList = new ArrayList<>();

      for (int i = 0; i < relatedToAppsList.size(); i++) {
        packageNamesList.add(relatedToAppsList.get(i).getPackageName());
      }

      final String[] packageNames = packageNamesList.toArray(new String[packageNamesList.size()]);

      if (installedAccessor != null) {
        return installedAccessor.get(packageNames).observeOn(Schedulers.computation());
      }
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
    return spannableFactory.createColorSpan(
        context.getString(R.string.displayable_social_timeline_article_related_to, appName),
        ContextCompat.getColor(context, R.color.appstimeline_grey), appName);
  }

  public void sendOpenBlogEvent() {
    timelineAnalytics.sendOpenBlogEvent(CARD_TYPE_NAME, getTitle(), getDeveloperLink().getUrl(),
        packageName);
  }

  public void sendOpenArticleEvent() {
    timelineAnalytics.sendOpenArticleEvent(CARD_TYPE_NAME, getTitle(), getLink().getUrl(),
        packageName);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_social_article;
  }

  @Override
  public void share(Context context, boolean privacyResult, ShareCardCallback shareCardCallback) {
    socialRepository.share(getTimelineCard(), context, privacyResult, shareCardCallback);
  }

  @Override public void share(Context context, ShareCardCallback shareCardCallback) {
    socialRepository.share(getTimelineCard(), context, shareCardCallback);
  }

  @Override public void like(Context context, String cardType, int rating) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating);
  }

  @Override public void like(Context context, String cardId, String cardType, int rating) {
    socialRepository.like(cardId, cardType, "", rating);
  }
}
