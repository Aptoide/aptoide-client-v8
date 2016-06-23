package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import lombok.Data;

@Data
public class Feature  {

	private final String title;
	private final String thumbnailUrl;
	private final String url;
	private final Date date;

	@JsonCreator
	public Feature(@JsonProperty("title") String title,
	               @JsonProperty("thumbnail") String thumbnailUrl,
	               @JsonProperty("url") String url,
	               @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date) {
		this.title = title;
		this.thumbnailUrl = thumbnailUrl;
		this.url = url;
		this.date = date;
	}
}
