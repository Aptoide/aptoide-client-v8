/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.billing;

public interface Product {

  int getId();

  String getIcon();

  String getTitle();

  Price getPrice();

  String getDescription();
}