/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by marcelobenites on 8/12/16.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InAppBillingPurchasesResponse extends BaseV3Response {

	@JsonProperty("publisher_response")
	private PublisherResponse publisherResponse;

	@Data
	public static class PublisherResponse {

		@JsonProperty("INAPP_PURCHASE_ITEM_LIST")
		private List<String> itemList;

		@JsonProperty("INAPP_PURCHASE_DATA_LIST")
		private List<PurchaseDataObject> purchaseDataList;

		@JsonProperty("INAAP_DATA_SIGNATURE_LIST")
		private List<String> signatureList;
	}

	@Data
	public static class PurchaseDataObject {
		private int orderId;
		private String productId;
		private String packageName;
		private long purchaseTime;
		private String purchaseState;
		private String developerPayload;
		private String token;
		private String purchaseToken;
	}
	
}
