/*
 * Copyright (c) 2016.
 * Modified on 13/05/2016.
 */

package cm.aptoide.pt.actions;

import java.lang.ref.WeakReference;
import rx.functions.Action1;

/**
 * Created on 13/05/16.
 */
public abstract class Action1WithWeakRef<T, W> implements Action1<T> {

  protected WeakReference<W> weakReference;

  public Action1WithWeakRef(W ref) {
    weakReference = new WeakReference<>(ref);
  }
}
