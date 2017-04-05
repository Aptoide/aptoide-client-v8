package cm.aptoide.pt.v8engine.view.timeline.displayable;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.model.v7.timeline.SocialRecommendation;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.ShareCardCallback;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import lombok.Getter;

/**
 * Created by jdandrade on 20/12/2016.
 */
public class SocialRecommendationDisplayable extends SocialCardDisplayable {

  @Getter private int avatarResource;
  @Getter private int titleResource;
  @Getter private Comment.User user;
  @Getter private long appId;
  @Getter private String packageName;
  @Getter private String appName;
  @Getter private String appIcon;
  @Getter private String abUrl;

  private SpannableFactory spannableFactory;
  private SocialRepository socialRepository;

  public SocialRecommendationDisplayable() {
  }

  public SocialRecommendationDisplayable(SocialRecommendation socialRecommendation,
      int avatarResource, Store store, int titleResource, Comment.User user, long appId,
      String packageName, String appName, String appIcon, String abUrl, long numberOfLikes,
      long numberOfComments, SpannableFactory spannableFactory, SocialRepository socialRepository,
      DateCalculator dateCalculator) {
    super(socialRecommendation, numberOfLikes, numberOfComments, store,
        socialRecommendation.getUser(), socialRecommendation.getUserSharer(),
        socialRecommendation.getMy().isLiked(), socialRecommendation.getLikes(),
        socialRecommendation.getDate(), spannableFactory, dateCalculator, abUrl);
    this.avatarResource = avatarResource;
    this.titleResource = titleResource;
    this.user = user;
    this.appId = appId;
    this.packageName = packageName;
    this.appName = appName;
    this.appIcon = appIcon;
    this.abUrl = abUrl;
    this.spannableFactory = spannableFactory;
    this.socialRepository = socialRepository;
  }

  public static Displayable from(SocialRecommendation socialRecommendation,
      SpannableFactory spannableFactory, SocialRepository socialRepository,
      DateCalculator dateCalculator) {

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
        socialRecommendation.getApp().getIcon(), abTestingURL,
        socialRecommendation.getStats().getLikes(), socialRecommendation.getStats().getComments(),
        spannableFactory, socialRepository, dateCalculator);
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
