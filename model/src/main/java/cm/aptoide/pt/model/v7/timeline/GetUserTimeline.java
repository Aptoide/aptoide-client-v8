package cm.aptoide.pt.model.v7.timeline;

import java.util.List;

import cm.aptoide.pt.model.v7.BaseV7Response;
import lombok.Data;


@Data
public class GetUserTimeline extends BaseV7Response {

	private List<TimelineItem> list;

}
