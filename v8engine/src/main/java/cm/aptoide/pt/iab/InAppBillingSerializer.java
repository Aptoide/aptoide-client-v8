/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 24/08/2016.
 */

package cm.aptoide.pt.iab;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;

public class InAppBillingSerializer {

	public List<String> serializeProducts(List<SKU> products) throws IOException {
		final List<String> serializedProducts = new ArrayList<String>();
		for (SKU product : products) {
			serializedProducts.add(new ObjectMapper().writeValueAsString(product));
		}
		return serializedProducts;
	}

	public List<String> serializePurchases(List<InAppBillingPurchasesResponse.InAppBillingPurchase> purchases) throws IOException {
		final List<String> serializedProducts = new ArrayList<String>();
		for (InAppBillingPurchasesResponse.InAppBillingPurchase purchase : purchases) {
			serializedProducts.add(serializePurchase(purchase));
		}
		return serializedProducts;
	}

	public String serializePurchase(InAppBillingPurchasesResponse.InAppBillingPurchase purchase) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(purchase);
	}
}