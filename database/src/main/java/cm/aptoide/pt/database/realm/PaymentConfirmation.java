/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/08/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by marcelobenites on 8/18/16.
 */
public class PaymentConfirmation extends RealmObject {

	public static final String PAYMENT_CONFIRMATION_ID = "paymentConfirmationId";
	public static final String PRODUCT_ID = "productId";

	@PrimaryKey private String paymentConfirmationId;

	private int paymentId;

	private double price;
	private String currency;
	private double taxRate;

	@Index private int productId;
	private String icon;
	private String title;
	private String description;
	private String priceDescription;

	private int apiVersion;
	private String sku;
	private String packageName;
	private String developerPayload;
	private String type;

	private long appId;
	private String storeName;

	public PaymentConfirmation() {
	}

	public PaymentConfirmation(String paymentConfirmationId, int paymentId, double price, String currency, double taxRate, int productId, String icon, String
			title, String description, String priceDescription) {
		this.paymentConfirmationId = paymentConfirmationId;
		this.paymentId = paymentId;
		this.price = price;
		this.currency = currency;
		this.taxRate = taxRate;
		this.productId = productId;
		this.icon = icon;
		this.title = title;
		this.description = description;
		this.priceDescription = priceDescription;
	}

	public void setApiVersion(int apiVersion) {
		this.apiVersion = apiVersion;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setDeveloperPayload(String developerPayload) {
		this.developerPayload = developerPayload;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getPaymentConfirmationId() {
		return paymentConfirmationId;
	}
	
	public int getPaymentId() {
		return paymentId;
	}
	
	public String getPriceDescription() {
		return priceDescription;
	}

	public double getPrice() {
		return price;
	}

	public int getApiVersion() {
		return apiVersion;
	}
	
	public String getSku() {
		return sku;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public String getDeveloperPayload() {
		return developerPayload;
	}
	
	public String getType() {
		return type;
	}
	
	public long getAppId() {
		return appId;
	}
	
	public String getStoreName() {
		return storeName;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public double getTaxRate() {
		return taxRate;
	}
	
	public int getProductId() {
		return productId;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}
}
