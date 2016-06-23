package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
public class Article extends Feature {

	@Getter private final Publisher publisher;

	@JsonCreator
	public Article(@JsonProperty("title") String title,
	               @JsonProperty("thumbnail") String thumbnailUrl,
	               @JsonProperty("publisher") Publisher publisher,
	               @JsonProperty("url") String url,
	               @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date) {
		super(title, thumbnailUrl, url, date);
		this.publisher = publisher;
	}

	protected boolean canEqual(Object other) {
		return other instanceof Article;
	}
}
