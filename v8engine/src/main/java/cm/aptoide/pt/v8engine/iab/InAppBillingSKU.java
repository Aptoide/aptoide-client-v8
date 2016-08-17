/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.iab;

import android.os.Parcel;
import android.os.Parcelable;

import cm.aptoide.pt.v8engine.payment.Product;
import lombok.Getter;

/**
 * Created by marcelobenites on 8/11/16.
 */
public class InAppBillingSKU {

	@Getter
	private String sku;

	@Getter
	private String priceDescription;

	@Getter
	private String title;

	@Getter
	private String description;

	public InAppBillingSKU(String sku, String priceDescription, String title, String description) {
		this.sku = sku;
		this.priceDescription = priceDescription;
		this.title = title;
		this.description = description;
	}

}