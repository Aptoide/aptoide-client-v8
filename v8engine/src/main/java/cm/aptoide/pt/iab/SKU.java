/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 24/08/2016.
 */

package cm.aptoide.pt.iab;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by marcelobenites on 8/11/16.
 */
@AllArgsConstructor
public class SKU {

	@Getter
	private String productId;

	@Getter
	private String type;

	@Getter
	private String price;

	@Getter
	@JsonProperty("price_currency")
	private String currency;

	@Getter
	@JsonProperty("price_amount_micros")
	private long amount;

	@Getter
	private String title;

	@Getter
	private String description;

}