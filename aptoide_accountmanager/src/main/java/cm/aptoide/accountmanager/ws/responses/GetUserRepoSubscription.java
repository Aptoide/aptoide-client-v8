/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/06/2016.
 */

package cm.aptoide.accountmanager.ws.responses;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by rmateus on 16-02-2015.
 */
@EqualsAndHashCode(callSuper = false)
// FIXME Warning: Generating equals/hashCode implementation but without a call to superclass, even though this class does not extend java.lang.Object. If
// this is intentional, add '@EqualsAndHashCode(callSuper=false)' to your type.
@Data public class GetUserRepoSubscription extends GenericResponseV3 {

  private String status;
  private List<Subscription> subscription;
}



