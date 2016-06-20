package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

@Data
public class FeatureTimelineItem implements TimelineItem<Feature> {

	private final List<Feature> features;

	@JsonCreator public FeatureTimelineItem(@JsonProperty("items") List<Feature> features) {
		this.features = features;
	}

	@Override
	public List<Feature> getItems() {
		return features;
	}
}
