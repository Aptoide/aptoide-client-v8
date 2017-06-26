/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessDatalistResponse;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true) public class GetUserTimeline
    extends BaseV7EndlessDatalistResponse<TimelineItem<TimelineCard>> {

}
