package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;

import cm.aptoide.pt.v8engine.link.Link;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import java.util.Date;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.timeline.Video;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by jdandrade on 8/10/16.
 */
@AllArgsConstructor
public class VideoDisplayable extends Displayable {

	@Getter private String videoTitle;
	@Getter private Link link;
	@Getter private Link baseLink;
	@Getter private String title;
	@Getter private String thumbnailUrl;
	@Getter private String avatarUrl;
	@Getter private long appId;

	private String appName;
	private Date date;
	private DateCalculator dateCalculator;
	private SpannableFactory spannableFactory;

	public static VideoDisplayable from(Video video, DateCalculator dateCalculator, SpannableFactory spannableFactory,
			LinksHandlerFactory linksHandlerFactory) {
		String appName = null;
		long appId = 0;
		if (video.getApps() != null && video.getApps().size() > 0) {
			appName = video.getApps().get(0).getName();
			appId = video.getApps().get(0).getId();
		}
		return new VideoDisplayable(video.getTitle(),
				linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, video.getUrl()),
				linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
						video.getPublisher().getBaseUrl()), video
				.getPublisher().getName(), video.getThumbnailUrl(), video.getPublisher()
				.getLogoUrl(), appId, appName, video.getDate(), dateCalculator, spannableFactory);
	}

	public VideoDisplayable() {
	}

	public String getTimeSinceLastUpdate(Context context) {
		return dateCalculator.getTimeSinceDate(context, date);
	}

	public boolean isGetApp() {
		return appName != null && appId != 0;
	}

	public Spannable getAppText(Context context) {
		return spannableFactory.createStyleSpan(context
				.getString(R.string.displayable_social_timeline_article_get_app_button, appName), Typeface.BOLD, appName);
	}

	public Spannable getAppRelatedText(Context context) {
		return spannableFactory.createColorSpan(context.getString(R.string.displayable_social_timeline_article_related_to, appName), ContextCompat.getColor
				(context, R.color.appstimeline_grey), appName);
	}

	@Override
	public Type getType() {
		return Type.SOCIAL_TIMELINE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_social_timeline_video;
	}

	public int getMarginWidth(Context context, int orientation){
		if (!context.getResources().getBoolean(R.bool.is_this_a_tablet_device)) {
			return 0;
		}

		int width = AptoideUtils.ScreenU.getCachedDisplayWidth(orientation);

		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return (int)(width * 0.2);
		} else {
			return (int)(width * 0.1);
		}
	}
}
