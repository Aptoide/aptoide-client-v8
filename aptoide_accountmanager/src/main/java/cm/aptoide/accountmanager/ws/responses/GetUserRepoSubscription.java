/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
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

	@Data
	public static class Subscription {

		private Number id;
		private String name;
		private String avatar;
		private String downloads;
		private String theme;
		private String description;
		private String items;
		private String view;
		private String avatarHd;
	}
}



