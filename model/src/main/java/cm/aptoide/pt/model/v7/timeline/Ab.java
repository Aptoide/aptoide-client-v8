package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * Created by jdandrade on 31/08/16.
 */
public class Ab {
	@Getter private final Conversion conversion;

	@JsonCreator
	public Ab(@JsonProperty("conversion") Conversion conversion) {
		this.conversion = conversion;
	}
}
