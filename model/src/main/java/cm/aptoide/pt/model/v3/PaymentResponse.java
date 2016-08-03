/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 03/08/2016.
 */

package cm.aptoide.pt.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import lombok.Data;

/**
 *
 * Created by j-pac on 19-02-2014.
 *
 * @author marcelobenites sithengineer j-pac
 *
 */
@Data
public class PaymentResponse {

	private String status;
	@JsonProperty("publisher_response") private PublisherResponse publisherResponse;

	@Data
	public static class PublisherResponse {

		@JsonProperty("RESPONSE_CODE") private int responseCode;
		@JsonProperty("INAPP_PURCHASE_ITEM_LIST") private ArrayList<String> item;
		@JsonProperty("INAPP_PURCHASE_DATA_LIST") private ArrayList<PurchaseDataObject> data;
		@JsonProperty("INAAP_DATA_SIGNATURE_LIST") private ArrayList<String> signature;
	}

	@Data
	public static class PurchaseDataObject {

		private int orderId;
		private String packageName;
		private String productId;
		private long purchaseTime;
		private String purchaseState;
		private String purchaseToken;
		private String developerPayload;
	}
}
