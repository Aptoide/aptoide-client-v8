/*
 * Copyright (c) 2016.
 * Modified on 08/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

/**
 * Created by neuro on 07-06-2016.
 */
public interface Endless {

  int DEFAULT_LIMIT = 10;

  int getOffset();

  void setOffset(int offset);

  Integer getLimit();
}
