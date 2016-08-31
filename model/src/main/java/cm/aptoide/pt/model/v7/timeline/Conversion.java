package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * Created by jdandrade on 31/08/16.
 */
public class Conversion {
	@Getter private final String url;

	@JsonCreator
	public Conversion(@JsonProperty("url") String url) {
		this.url = url;
	}
}
