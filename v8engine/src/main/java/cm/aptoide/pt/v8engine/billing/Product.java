/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.billing;

public interface Product {

  String getId();

  String getIcon();

  String getTitle();

  Price getPrice();

  String getDescription();
}