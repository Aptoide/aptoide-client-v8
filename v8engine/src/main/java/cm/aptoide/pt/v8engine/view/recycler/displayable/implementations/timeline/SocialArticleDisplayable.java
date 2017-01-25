package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.model.v7.timeline.SocialArticle;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.link.Link;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.repository.TimelineMetricsManager;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.DateCalculator;
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
  private TimelineMetricsManager timelineMetricsManager;
  private SocialRepository socialRepository;

  public SocialArticleDisplayable() {
  }

  public SocialArticleDisplayable(SocialArticle socialArticle, String articleTitle, Link link,
      Link developerLink, String title, String thumbnailUrl, String avatarUrl, long appId,
      String abUrl, Store store, Comment.User user, long numberOfLikes, long numberOfComments,
      List<App> relatedToAppsList, Date date, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, TimelineMetricsManager timelineMetricsManager,
      SocialRepository socialRepository) {
    super(socialArticle, numberOfLikes, numberOfComments, store, user,
        socialArticle.getUserSharer(), date, spannableFactory, dateCalculator);
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
    this.timelineMetricsManager = timelineMetricsManager;
    this.socialRepository = socialRepository;
  }

  public static SocialArticleDisplayable from(SocialArticle socialArticle,
      DateCalculator dateCalculator, SpannableFactory spannableFactory,
      LinksHandlerFactory linksHandlerFactory, TimelineMetricsManager timelineMetricsManager,
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
        abTestingURL, socialArticle.getStore(), socialArticle.getUser(), socialArticle.getLikes(),
        socialArticle.getComments(), socialArticle.getApps(), socialArticle.getDate(),
        dateCalculator, spannableFactory, timelineMetricsManager, socialRepository);
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

  public void sendOpenArticleEvent(SendEventRequest.Body.Data data, String eventName) {
    timelineMetricsManager.sendEvent(data, eventName);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_social_article;
  }

  @Override public void like(Context context, String cardType, int rating) {
    socialRepository.like(getTimelineCard(), cardType, "", rating);
  }

  @Override public void share(Context context, boolean privacyResult) {
    socialRepository.share(getTimelineCard(), context, privacyResult);
  }
}
