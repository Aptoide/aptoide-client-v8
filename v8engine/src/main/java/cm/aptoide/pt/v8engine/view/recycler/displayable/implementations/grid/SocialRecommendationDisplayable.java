package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.model.v7.timeline.SocialRecommendation;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.repository.TimelineMetricsManager;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.SocialCardDisplayable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by jdandrade on 20/12/2016.
 */
@AllArgsConstructor public class SocialRecommendationDisplayable extends SocialCardDisplayable {
  private SocialRecommendation socialRecommendation;
  @Getter private int avatarResource;
  @Getter private Store store;
  @Getter private int titleResource;
  @Getter private Comment.User user;
  @Getter private long appId;
  @Getter private String packageName;
  @Getter private String appName;
  @Getter private String appIcon;
  @Getter private String abUrl;
  @Getter private long numberOfLikes;
  @Getter private long numberOfComments;

  private TimelineMetricsManager timelineMetricsManager;
  private SpannableFactory spannableFactory;
  private SocialRepository socialRepository;

  public SocialRecommendationDisplayable() {
  }

  public static Displayable from(SocialRecommendation socialRecommendation,
      TimelineMetricsManager timelineMetricsManager, SpannableFactory spannableFactory,
      SocialRepository socialRepository) {

    //for (App similarApp : socialRecommendation.getSimilarApps()) {
    //  similarAppsNames.add(similarApp.getName());
    //  similarPackageNames.add(similarApp.getPackageName());
    //}

    String abTestingURL = null;

    if (socialRecommendation.getAb() != null
        && socialRecommendation.getAb().getConversion() != null
        && socialRecommendation.getAb().getConversion().getUrl() != null) {
      abTestingURL = socialRecommendation.getAb().getConversion().getUrl();
    }

    return new SocialRecommendationDisplayable(socialRecommendation,
        Application.getConfiguration().getIcon(), socialRecommendation.getStore(),
        R.string.displayable_social_timeline_recommendation_atptoide_team_recommends,
        socialRecommendation.getUser(), socialRecommendation.getApp().getId(),
        socialRecommendation.getApp().getPackageName(), socialRecommendation.getApp().getName(),
        socialRecommendation.getApp().getIcon(), abTestingURL, socialRecommendation.getLikes(),
        socialRecommendation.getComments(), timelineMetricsManager, spannableFactory,
        socialRepository);
  }

  public String getTitle() {
    return AptoideUtils.StringU.getFormattedString(titleResource,
        Application.getConfiguration().getMarketName());
  }

  public Spannable getAppText(Context context) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
        ContextCompat.getColor(context, R.color.appstimeline_grey), "");
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_social_recommendation;
  }

  public void sendClickEvent(SendEventRequest.Body.Data data, String eventName) {
    timelineMetricsManager.sendEvent(data, eventName);
  }

  @Override public void share(Context context, boolean privacyResult) {

  }

  @Override public void like(Context context, String cardType, int rating) {
    socialRepository.like(socialRecommendation, cardType, "", rating);
  }
}
