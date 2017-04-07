/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import rx.Observable;

/**
 * Created on 02/09/16.
 */
public interface Repository<E, Tkey> {

  void save(E entity);

  Observable<E> get(Tkey id);
}
