package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

@Data
public class FeatureTimelineItem implements TimelineItem<Feature> {

	private final Feature feature;

	@JsonCreator public FeatureTimelineItem(@JsonProperty("data") Feature feature) {
		this.feature = feature;
	}

	@Override
	public Feature getData() {
		return feature;
	}
}
