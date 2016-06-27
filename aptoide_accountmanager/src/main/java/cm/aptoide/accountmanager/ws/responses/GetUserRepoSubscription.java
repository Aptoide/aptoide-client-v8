/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/06/2016.
 */

package cm.aptoide.accountmanager.ws.responses;

import java.util.List;

import lombok.Data;

/**
 * Created by rmateus on 16-02-2015.
 */
@Data
public class GetUserRepoSubscription extends GenericResponseV3 {

	private String status;
	private List<Subscription> subscription;
}



