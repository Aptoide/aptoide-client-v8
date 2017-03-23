package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.timeline.SocialVideo;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.ShareCardCallback;
import cm.aptoide.pt.v8engine.link.Link;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.repository.TimelineAnalytics;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.DateCalculator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 28/11/2016.
 */
public class SocialVideoDisplayable extends SocialCardDisplayable {

  public static final String CARD_TYPE_NAME = "SOCIAL_VIDEO";
  @Getter private String videoTitle;
  @Getter private Link link;
  @Getter private Link baseLink;
  @Getter private String title;
  @Getter private String thumbnailUrl;
  @Getter private String avatarUrl;
  @Getter private long appId;
  @Getter private String abUrl;
  @Getter private Comment.User user;
  @Getter private Comment.User userSharer;

  @Getter private List<App> relatedToAppsList;
  private Date date;
  private DateCalculator dateCalculator;
  private SpannableFactory spannableFactory;
  private TimelineAnalytics timelineAnalytics;
  private SocialRepository socialRepository;

  public SocialVideoDisplayable() {
  }

  private SocialVideoDisplayable(SocialVideo socialVideo, String videoTitle, Link link,
      Link baseLink, String publisherName, String thumbnailUrl, String publisherAvatarUrl,
      long appId, String abUrl, Comment.User user, long numberOfLikes, long numberOfComments,
      List<App> relatedToAppsList, Date date, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository) {
    super(socialVideo, numberOfLikes, numberOfComments, socialVideo.getStore(),
        socialVideo.getUser(), socialVideo.getUserSharer(), socialVideo.getMy().isLiked(),
        socialVideo.getLikes(), date, spannableFactory, dateCalculator, abUrl);
    this.videoTitle = videoTitle;
    this.link = link;
    this.baseLink = baseLink;
    this.title = publisherName;
    this.thumbnailUrl = thumbnailUrl;
    this.avatarUrl = publisherAvatarUrl;
    this.appId = appId;
    this.abUrl = abUrl;
    this.user = user;
    this.userSharer = socialVideo.getUserSharer();
    this.relatedToAppsList = relatedToAppsList;
    this.date = date;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.timelineAnalytics = timelineAnalytics;
    this.socialRepository = socialRepository;
  }

  public static SocialVideoDisplayable from(SocialVideo socialVideo, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, LinksHandlerFactory linksHandlerFactory,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository) {
    long appId = 0;

    String abTestingURL = null;

    if (socialVideo.getAb() != null
        && socialVideo.getAb().getConversion() != null
        && socialVideo.getAb().getConversion().getUrl() != null) {
      abTestingURL = socialVideo.getAb().getConversion().getUrl();
    }

    return new SocialVideoDisplayable(socialVideo, socialVideo.getTitle(),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, socialVideo.getUrl()),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            socialVideo.getPublisher().getBaseUrl()), socialVideo.getPublisher().getName(),
        socialVideo.getThumbnailUrl(), socialVideo.getPublisher().getLogoUrl(), appId, abTestingURL,
        socialVideo.getUser(), socialVideo.getStats().getLikes(),
        socialVideo.getStats().getComments(), socialVideo.getApps(), socialVideo.getDate(),
        dateCalculator, spannableFactory, timelineAnalytics, socialRepository);
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

  public Spannable getSharedBy(Context context) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.social_timeline_shared_by, userSharer.getName()),
        ContextCompat.getColor(context, R.color.black), userSharer.getName());
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
    return R.layout.displayable_social_timeline_social_video;
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

  @Override public void like(Context context, String cardType, int rating) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating);
  }

  @Override public void like(Context context, String cardId, String cardType, int rating) {
    socialRepository.like(cardId, cardType, "", rating);
  }
}
