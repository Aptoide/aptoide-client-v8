/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/08/2016.
 */

package cm.aptoide.pt.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by marcelobenites on 7/27/16.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetApkInfoJson extends BaseV3Response {

	@JsonProperty("payment")
	private Payment payment;

	@Data
	public static class Payment {

		@JsonProperty("amount")
		private Double amount;

		@JsonProperty("currency_symbol")
		private String symbol;

		@JsonProperty("apkpath")
		private String apkPath;

		@JsonProperty("metadata")
		private Metadata metadata;

		@JsonProperty("payment_services")
		private List<PaymentService> paymentServices;

		@JsonProperty("status")
		private String status;

		public boolean isPaid() {
			return status.equalsIgnoreCase("OK");
		}

		public String getPrice() {
			return symbol + " " + String.valueOf(amount);
		}
	}

	@Data
	public static class Metadata {

		@JsonProperty("id")
		private int id;
	}
}
