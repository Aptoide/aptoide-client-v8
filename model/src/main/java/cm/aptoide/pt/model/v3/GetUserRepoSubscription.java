/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/06/2016.
 */

package cm.aptoide.pt.model.v3;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by rmateus on 16-02-2015.
 */
@EqualsAndHashCode(callSuper = false) @Data public class GetUserRepoSubscription
    extends BaseV3Response {

  private List<Subscription> subscription;
}



