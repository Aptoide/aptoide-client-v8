package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

@Data
public class ArticleTimelineItem implements TimelineItem<Article> {

	private final List<Article> articles;

	@JsonCreator
	public ArticleTimelineItem(@JsonProperty("items") List<Article> articles) {
		this.articles = articles;
	}

	@Override
	public List<Article> getItems() {
		return articles;
	}
}
