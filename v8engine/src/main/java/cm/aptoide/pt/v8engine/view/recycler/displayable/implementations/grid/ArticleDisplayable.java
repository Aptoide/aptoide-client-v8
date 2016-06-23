package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import java.util.Date;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.ArticleTimelineItem;
import cm.aptoide.pt.model.v7.timeline.TimelineItem;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class ArticleDisplayable extends Displayable {

	private Article article;
	private DateCalculator dateCalculator;

	public ArticleDisplayable() {
	}

	public ArticleDisplayable(Article article, DateCalculator dateCalculator) {
		this.article = article;
		this.dateCalculator = dateCalculator;
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

	public int getHoursSinceLastUpdate() {
		return dateCalculator.getHoursSinceDate(article.getDate());
	}

	public String getAvatarUrl() {
		return article.getPublisher().getLogoUrl();
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
