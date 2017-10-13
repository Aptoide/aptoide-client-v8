/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/08/2016.
 */

package cm.aptoide.pt.billing;

public interface Purchase {

  String getProductId();

  boolean isNew();

  boolean isCompleted();

  boolean isPending();

  boolean isFailed();
}
