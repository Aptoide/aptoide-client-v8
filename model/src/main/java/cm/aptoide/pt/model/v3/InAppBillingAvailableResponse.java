/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/08/2016.
 */

package cm.aptoide.pt.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by marcelobenites on 8/25/16.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InAppBillingAvailableResponse extends BaseV3Response {

	@JsonProperty("response")
	private InAppBillingAvailable inAppBillingAvailable;

	@Data
	public static class InAppBillingAvailable {

		@JsonProperty("iabavailable")
		private String inAppBillingAvailable;

		public boolean isAvailable() {
			return inAppBillingAvailable!=null && inAppBillingAvailable.equalsIgnoreCase("ok");
		}
	}
}
