/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 24/08/2016.
 */

package cm.aptoide.pt.iab;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class InAppBillingSerializer {

	public List<String> serializeProducts(List<InAppBillingSKU> products) {
		final List<String> serializedProducts = new ArrayList<String>();
		try {
			for (InAppBillingSKU product : products) {
				serializedProducts.add(new ObjectMapper().writeValueAsString(product));
			}
		} catch (JsonProcessingException ignored) {}
		return serializedProducts;
	}

	public List<String> serializePurchases(List<InAppBillingPurchase> purchases) {
		final List<String> serializedProducts = new ArrayList<String>();
		try {
			for (InAppBillingPurchase purchase : purchases) {
				serializedProducts.add(new ObjectMapper().writeValueAsString(purchase));
			}
		} catch (JsonProcessingException ignored) {}
		return serializedProducts;
	}
}