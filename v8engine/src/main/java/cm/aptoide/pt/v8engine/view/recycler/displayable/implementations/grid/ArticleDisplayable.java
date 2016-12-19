package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.link.Link;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.repository.TimelineMetricsManager;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 6/17/16.
 */
@AllArgsConstructor public class ArticleDisplayable extends CardDisplayable {

  private Article article;
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
  private TimelineMetricsManager timelineMetricsManager;
  private SocialRepository socialRepository;

  public ArticleDisplayable() {
  }

  public static ArticleDisplayable from(Article article, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, LinksHandlerFactory linksHandlerFactory,
      TimelineMetricsManager timelineMetricsManager, SocialRepository socialRepository) {
    long appId = 0;
    //if (article.getApps() != null && article.getApps().size() > 0) {
    //  appName = article.getApps().get(0).getName();
    //  appId = article.getApps().get(0).getId();
    //}

    String abTestingURL = null;

    if (article.getAb() != null
        && article.getAb().getConversion() != null
        && article.getAb().getConversion().getUrl() != null) {
      abTestingURL = article.getAb().getConversion().getUrl();
    }

    return new ArticleDisplayable(article, article.getCardId(), article.getTitle(),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, article.getUrl()),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            article.getPublisher().getBaseUrl()), article.getPublisher().getName(),
        article.getThumbnailUrl(), article.getPublisher().getLogoUrl(), appId, abTestingURL,
        article.getApps(), article.getDate(), dateCalculator, spannableFactory,
        timelineMetricsManager, socialRepository);
  }

  public Observable<List<Installed>> getRelatedToApplication() {
    if (relatedToAppsList != null && relatedToAppsList.size() > 0) {
      InstalledAccessor installedAccessor = AccessorFactory.getAccessorFor(Installed.class);
      List<String> packageNamesList = new ArrayList<String>();

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

  public String getTimeSinceLastUpdate(Context context) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

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
    return R.layout.displayable_social_timeline_article;
  }

  @Override public void share(Context context) {
    socialRepository.share(article, context);
  }

}
