package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

import cm.aptoide.pt.model.v7.listapp.App;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(exclude = {"publisher"}, callSuper = true)
public class Video extends Feature {

	@Getter private final Publisher publisher;

	@JsonCreator
	public Video(@JsonProperty("uid") String cardId,
	             @JsonProperty("title") String title,
	             @JsonProperty("thumbnail") String thumbnailUrl,
	             @JsonProperty("publisher") Publisher publisher,
	             @JsonProperty("url") String url,
	             @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
	             @JsonProperty("apps") List<App> apps) {
		super(cardId, title, thumbnailUrl, url, date, apps);
		this.publisher = publisher;
	}
}