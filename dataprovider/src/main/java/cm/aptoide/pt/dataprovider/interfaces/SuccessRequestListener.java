/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/06/2016.
 */

package cm.aptoide.pt.dataprovider.interfaces;

import rx.functions.Action1;

/**
 * Request onSuccess Interface.
 */
public interface SuccessRequestListener<T> extends Action1<T> {

  void call(T t);
}
