/*
 * Copyright (c) 2016.
 * Modified on 27/04/2016.
 */

package cm.aptoide.pt.dataprovider.cache;

/**
 * Created on 27/04/16.
 */
public interface KeyAlgorithm<Tin, Tout> {
  Tout getKeyFrom(Tin data);
}
