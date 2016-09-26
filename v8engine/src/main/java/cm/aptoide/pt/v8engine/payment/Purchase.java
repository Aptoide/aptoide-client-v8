/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import java.io.IOException;

/**
 * Created by marcelobenites on 8/25/16.
 */
public interface Purchase {

  public String getData() throws IOException;

  public String getSignature();
}