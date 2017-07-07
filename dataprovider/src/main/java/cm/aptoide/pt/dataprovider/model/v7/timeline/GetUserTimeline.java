/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessDataListResponse;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true) public class GetUserTimeline
    extends BaseV7EndlessDataListResponse<TimelineItem<TimelineCard>> {

}
