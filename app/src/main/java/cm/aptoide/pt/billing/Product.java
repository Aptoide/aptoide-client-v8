/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.billing;

public interface Product {

  long getProductId();

  String getIcon();

  String getTitle();

  Price getPrice();

  String getDescription();
}