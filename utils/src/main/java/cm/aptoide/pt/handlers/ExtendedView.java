/*
 * Copyright (c) 2016.
 * Modified on 06/05/2016.
 */

package cm.aptoide.pt.handlers;

import android.view.View;
import java.lang.ref.WeakReference;

/**
 * Created on 06/05/16.
 */
public abstract class ExtendedView<T> {

  private WeakReference<T> weakReference;

  public ExtendedView(T ref) {
    weakReference = new WeakReference<>(ref);
  }

  public class OnClickListener implements View.OnClickListener {

    @Override public void onClick(View v) {
      T ref = weakReference.get();
      if (ref != null) {
        onClickReference(v, ref);
      } else {
        onNullReference(v);
      }
    }

    public void onClickReference(View v, T ref) {
    }

    public void onNullReference(View v) {
    }
  }

	/*

	usage:

	new ExtendedView<Object>(obj){}.new OnClickListener() {

		@Override
		public void onClickReference(View v, Object ref) {
			Context ctx = v.getContext();
			ctx.startActivity(new Intent(ctx, Activity.class));
		}

		@Override
		public void onNullReference(View v) {
			Logger.e(TAG, "reference was GC'ed");
		}
	}

	 */
}
