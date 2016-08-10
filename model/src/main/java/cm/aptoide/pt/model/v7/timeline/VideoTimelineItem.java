/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 06/07/2016.
 */

package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class VideoTimelineItem implements TimelineItem<TimelineCard> {

	private final Video video;

	@JsonCreator
	public VideoTimelineItem(@JsonProperty("data") Video video) {
		this.video = video;
	}

	@Override
	public Video getData() {
		return video;
	}
}
