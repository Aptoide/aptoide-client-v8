/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import java.io.IOException;

public interface Purchase {

  public String getData() throws IOException;

  public String getSignature();
}