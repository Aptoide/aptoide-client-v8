package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.style.StyleSpan;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class ArticleDisplayable extends Displayable {

	private SpannableFactory spannableFactory;
	private Article article;
	private DateCalculator dateCalculator;

	public ArticleDisplayable() {
	}

	public ArticleDisplayable(Article article, DateCalculator dateCalculator, SpannableFactory spannableFactory) {
		this.article = article;
		this.dateCalculator = dateCalculator;
		this.spannableFactory = spannableFactory;
	}

	public String getTitle() {
		return article.getTitle();
	}

	public String getUrl() {
		return article.getUrl();
	}

	public String getPublisher() {
		return article.getPublisher().getName();
	}

	public String getThumbnailUrl() {
		return article.getThumbnailUrl();
	}

	public String getHoursSinceLastUpdate(Context context) {
		return context.getString(R.string.fragment_social_timeline_hours_since_last_update, dateCalculator
				.getHoursSinceDate(article.getDate()));
	}

	public String getAvatarUrl() {
		return article.getPublisher().getLogoUrl();
	}

	public Spannable getAppText(Context context) {
		return spannableFactory.create(context
				.getString(R.string.displayable_social_timeline_article_get_app_button, "Clash of Clans"), "Clash of Clans", new StyleSpan(Typeface.BOLD));
	}

	public long getAppId() {
		return 19347406;
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
