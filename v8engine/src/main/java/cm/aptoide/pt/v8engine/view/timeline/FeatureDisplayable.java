package cm.aptoide.pt.v8engine.view.timeline;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.text.Spannable;
import cm.aptoide.pt.model.v7.timeline.Feature;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.Date;
import lombok.Getter;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class FeatureDisplayable extends Displayable {

  @Getter private int avatarResource;
  @Getter private int titleResource;
  @Getter private String thumbnailUrl;
  @Getter private String url;

  private String appName;
  private String title;
  private Date date;
  private DateCalculator dateCalculator;
  private SpannableFactory spannableFactory;

  public FeatureDisplayable() {
  }

  public FeatureDisplayable(int avatarResource, int titleResource, String thumbnailUrl, String url,
      String appName, String title, Date date, DateCalculator dateCalculator,
      SpannableFactory spannableFactory) {
    this.avatarResource = avatarResource;
    this.titleResource = titleResource;
    this.thumbnailUrl = thumbnailUrl;
    this.url = url;
    this.appName = appName;
    this.title = title;
    this.date = date;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
  }

  public static FeatureDisplayable from(Feature feature, DateCalculator dateCalculator,
      SpannableFactory spannableFactory) {
    String appName = null;
    long appId = 0;
    if (feature.getApps() != null && feature.getApps().size() > 0) {
      appName = feature.getApps().get(0).getName();
      appId = feature.getApps().get(0).getId();
    }
    return new FeatureDisplayable(Application.getConfiguration().getIcon(),
        R.string.fragment_social_timeline_aptoide_team, feature.getThumbnailUrl(), feature.getUrl(),
        appName, feature.getTitle(), feature.getDate(), dateCalculator, spannableFactory);
  }

  public int getMarginWidth(Context context, int orientation) {
    if (!context.getResources().getBoolean(R.bool.is_this_a_tablet_device)) {
      return 0;
    }

    int width = AptoideUtils.ScreenU.getCachedDisplayWidth(orientation);

    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
      return (int) (width * 0.2);
    } else {
      return (int) (width * 0.1);
    }
  }

  public String getTitle(Context context) {
    return context.getString(titleResource);
  }

  public String getTimeSinceLastUpdate(Context context) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  public Spannable getAppText(Context context) {
    return spannableFactory.createStyleSpan(
        context.getString(R.string.displayable_social_timeline_article_get_app_button, appName),
        Typeface.BOLD, appName);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_feature;
  }
}
