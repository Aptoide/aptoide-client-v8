/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by marcelobenites on 8/11/16.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InAppBillingSkuDetailsResponse extends BaseV3Response {

	private Metadata metadata;

	@JsonProperty("publisher_response")
	private PublisherResponse publisherResponse;

	@JsonProperty("payment_services")
	private List<PaymentService> paymentServices;

	@Data
	public static class PublisherResponse {

		@JsonProperty("DETAILS_LIST")
		private List<PurchaseDataObject> detailList;
	}

	@Data
	public static class PurchaseDataObject {

		@JsonProperty("productId")
		private String productId;
		private String price;
		private String title;
		private String description;
		private String type;
	}

	@Data
	public static class Metadata {
		private int id;
		private String icon;
	}
	
}
