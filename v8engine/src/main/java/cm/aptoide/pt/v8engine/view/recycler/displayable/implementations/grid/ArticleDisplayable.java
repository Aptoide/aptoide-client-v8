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
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by marcelobenites on 6/17/16.
 */
@AllArgsConstructor
public class ArticleDisplayable extends Displayable {

	@Getter private String articleTitle;
	@Getter private Link link;
	@Getter private Link developerLink;
	@Getter private String title;
	@Getter private String thumbnailUrl;
	@Getter private String avatarUrl;
	@Getter private long appId;

	private String appName;
	private Date date;
	private DateCalculator dateCalculator;
	private SpannableFactory spannableFactory;

	public static ArticleDisplayable from(Article article, DateCalculator dateCalculator,
			SpannableFactory spannableFactory, LinksHandlerFactory linksHandlerFactory) {
		String appName = null;
		long appId = 0;
		if (article.getApps() != null && article.getApps().size() > 0) {
			appName = article.getApps().get(0).getName();
			appId = article.getApps().get(0).getId();
		}
		return new ArticleDisplayable(article.getTitle(),
				linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, article.getUrl()),
				linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
						article.getPublisher().getBaseUrl()), article
				.getPublisher().getName(), article.getThumbnailUrl(), article.getPublisher()
				.getLogoUrl(), appId, appName, article.getDate(), dateCalculator, spannableFactory);
	}

	public ArticleDisplayable() {
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

	public Spannable getAppRelatedToText(Context context) {
		return spannableFactory.createColorSpan(context.getString(R.string.displayable_social_timeline_article_related_to, appName), ContextCompat.getColor
				(context, R.color.appstimeline_grey), appName);
	}

	@Override
	public Type getType() {
		return Type.SOCIAL_TIMELINE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_social_timeline_article;
	}
}
