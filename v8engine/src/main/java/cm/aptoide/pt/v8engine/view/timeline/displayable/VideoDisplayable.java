package cm.aptoide.pt.v8engine.view.timeline.displayable;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.timeline.Video;
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
 * Created by jdandrade on 8/10/16.
 */
public class VideoDisplayable extends CardDisplayable {

  public static final String CARD_TYPE_NAME = "VIDEO";
  @Getter private String videoTitle;
  @Getter private Link link;
  @Getter private Link baseLink;
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

  public VideoDisplayable() {
  }

  public VideoDisplayable(Video video, String videoTitle, Link link, Link baseLink, String title,
      String thumbnailUrl, String avatarUrl, long appId, String abUrl, List<App> relatedToAppsList,
      Date date, DateCalculator dateCalculator, SpannableFactory spannableFactory,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository) {
    super(video);
    this.videoTitle = videoTitle;
    this.link = link;
    this.baseLink = baseLink;
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
  }

  public static VideoDisplayable from(Video video, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, LinksHandlerFactory linksHandlerFactory,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository) {
    long appId = 0;

    String abTestingURL = null;

    if (video.getAb() != null
        && video.getAb().getConversion() != null
        && video.getAb().getConversion().getUrl() != null) {
      abTestingURL = video.getAb().getConversion().getUrl();
    }

    return new VideoDisplayable(video, video.getTitle(),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, video.getUrl()),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            video.getPublisher().getBaseUrl()), video.getPublisher().getName(),
        video.getThumbnailUrl(), video.getPublisher().getLogoUrl(), appId, abTestingURL,
        video.getApps(), video.getDate(), dateCalculator, spannableFactory, timelineAnalytics,
        socialRepository);
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

  public Spannable getAppText(Context context, String appName) {
    return spannableFactory.createStyleSpan(
        context.getString(R.string.displayable_social_timeline_article_get_app_button, appName),
        Typeface.BOLD, appName);
  }

  public Spannable getAppRelatedText(Context context, String appName) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.displayable_social_timeline_article_related_to, appName),
        ContextCompat.getColor(context, R.color.appstimeline_grey), appName);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_video;
  }

  public void sendOpenVideoEvent(String packageName) {
    timelineAnalytics.sendOpenVideoEvent(CARD_TYPE_NAME, getTitle(), getBaseLink().getUrl(),
        packageName);
  }

  public void sendOpenChannelEvent(String packageName) {
    timelineAnalytics.sendOpenChannelEvent(CARD_TYPE_NAME, getTitle(), getBaseLink().getUrl(),
        packageName);
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
