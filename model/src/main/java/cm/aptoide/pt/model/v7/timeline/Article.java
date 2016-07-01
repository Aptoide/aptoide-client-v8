package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

import cm.aptoide.pt.model.v7.listapp.App;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
public class Article extends Feature {

	@Getter private final Publisher publisher;

	@JsonCreator
	public Article(@JsonProperty("uid") String id,
	               @JsonProperty("title") String title,
	               @JsonProperty("thumbnail") String thumbnailUrl,
	               @JsonProperty("publisher") Publisher publisher,
	               @JsonProperty("url") String url,
	               @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
	               @JsonProperty("apps") List<App> apps) {
		super(id, title, thumbnailUrl, url, date, apps);
		this.publisher = publisher;
	}
}