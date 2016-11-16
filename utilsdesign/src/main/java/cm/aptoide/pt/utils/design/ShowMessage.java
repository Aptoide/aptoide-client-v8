/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/08/2016.
 */

package cm.aptoide.pt.utils.design;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;
import cm.aptoide.pt.logger.Logger;
import java.lang.annotation.Retention;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by trinkes on 5/9/16.
 */
public class ShowMessage {

  private static final String TAG = ShowMessage.class.getSimpleName();

  //
  // override 1
  //

  @NonNull private static Snackbar asSnackInternal(View view, String msg, String actionMsg,
      View.OnClickListener action) {
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setAction(actionMsg, action);
  }

  public static void asSnack(View view, String msg, String actionMsg, View.OnClickListener action) {
    asSnackInternal(view, msg, actionMsg, action).show();
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(View view, String msg,
      String actionMsg, View.OnClickListener action) {
    return asSnackObservableInternal(asSnackInternal(view, msg, actionMsg, action));
  }

  //
  // override 2
  //

  @NonNull private static Snackbar asSnackInternal(View view, String msg) {
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
  }

  public static void asSnack(View view, String msg) {
    asSnackInternal(view, msg).show();
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(View view, String msg) {
    return asSnackObservableInternal(asSnackInternal(view, msg));
  }

  //
  // override 3
  //

  private static Snackbar asSnackInternal(View view, @StringRes int msg, @StringRes int actionMsg,
      View.OnClickListener action) {
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setAction(actionMsg, action);
  }

  public static void asSnack(View view, @StringRes int msg, @StringRes int actionMsg,
      View.OnClickListener action) {
    asSnackInternal(view, msg, actionMsg, action).show();
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(View view, @StringRes int msg,
      @StringRes int actionMsg, View.OnClickListener action) {
    return asSnackObservableInternal(asSnackInternal(view, msg, actionMsg, action));
  }

  //
  // override 4
  //

  private static Snackbar asSnackInternal(View view, @StringRes int msg) {
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
  }

  public static void asSnack(View view, @StringRes int msg) {
    asSnackInternal(view, msg).show();
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(View view, @StringRes int msg) {
    return asSnackObservableInternal(asSnackInternal(view, msg));
  }

  //
  // override 5
  //

  @NonNull private static Snackbar asSnackInternal(Activity activity, String msg) {
    View view = getViewFromActivity(activity);
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
  }

  public static void asSnack(Activity activity, String msg) {
    asSnackInternal(activity, msg).show();
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(Activity activity, String msg) {
    return asSnackObservableInternal(asSnackInternal(activity, msg));
  }

  //
  // override 6
  //

  private static Snackbar asSnackInternal(Activity activity, @StringRes int msg) {
    View view = getViewFromActivity(activity);
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
  }

  public static void asSnack(Activity activity, @StringRes int msg) {
    asSnackInternal(activity, msg).show();
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(Activity activity,
      @StringRes int msg) {
    return asSnackObservableInternal(asSnackInternal(activity, msg));
  }

  //
  // override 7
  //

  @NonNull private static Snackbar asSnackInternal(Fragment fragment, String msg) {
    return Snackbar.make(fragment.getView(), msg, Snackbar.LENGTH_SHORT);
  }

  public static void asSnack(Fragment fragment, String msg) {
    asSnackInternal(fragment, msg).show();
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(Fragment fragment, String msg) {
    return asSnackObservableInternal(asSnackInternal(fragment, msg));
  }

  //
  // override 8
  //

  @NonNull private static Snackbar asSnackInternal(Fragment fragment, @StringRes int msg) {
    return Snackbar.make(fragment.getView(), msg, Snackbar.LENGTH_SHORT);
  }

  public static void asSnack(Fragment fragment, @StringRes int msg) {
    asSnackInternal(fragment, msg).show();
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(Fragment fragment, @StringRes int msg) {
    return asSnackObservableInternal(asSnackInternal(fragment, msg));
  }

  //
  // override 9
  //

  private static Snackbar asSnackInternal(Activity activity, int msg, int actionMsg,
      View.OnClickListener action) {
    View view = getViewFromActivity(activity);
    if (view == null) {
      return null;
    }
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setAction(actionMsg, action);
  }

  public static void asSnack(Activity activity, int msg, int actionMsg,
      View.OnClickListener action) {
    Snackbar snackbar = asSnackInternal(activity, msg, actionMsg, action);
    if (snackbar != null) {
      snackbar.show();
    }
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(Activity activity, int msg,
      int actionMsg, View.OnClickListener action) {
    Snackbar snackbar = asSnackInternal(activity, msg, actionMsg, action);
    if (snackbar != null) {
      return asSnackObservableInternal(snackbar);
    }
    return Observable.error(new IllegalStateException("Extracted view from activity is null"));
  }

  //
  // base observable Snackbar
  //

  private static Observable<Integer> asSnackObservableInternal(Snackbar snackbar) {

    return Observable.create(new Observable.OnSubscribe<Integer>() {
      @Override public void call(Subscriber<? super Integer> subscriber) {
        Snackbar.Callback snackbarCallback = new Snackbar.Callback() {
          @Override public void onDismissed(Snackbar snackbar, int event) {
            super.onDismissed(snackbar, event);
            if (!subscriber.isUnsubscribed()) {
              subscriber.onNext(DISMISSED);
            }
          }

          @Override public void onShown(Snackbar snackbar) {
            super.onShown(snackbar);
            if (!subscriber.isUnsubscribed()) {
              subscriber.onNext(VISIBLE);
            }
          }
        };

        snackbar.setCallback(snackbarCallback);

        subscriber.add(Subscriptions.create(() -> {
          snackbar.setCallback(null);
          snackbar.dismiss();
        }));

        snackbar.show();
      }
    });
  }

  @Retention(SOURCE) @IntDef({ VISIBLE, DISMISSED }) public @interface SnackbarVisibility {
  }

  public static final int VISIBLE = 0;
  public static final int DISMISSED = 1;

  @Nullable private static View getViewFromActivity(Activity activity) {
    View view = activity.getCurrentFocus();
    if (view == null) {
      view = activity.findViewById(android.R.id.content);
    }
    if (view == null) {
      Logger.e(TAG, new IllegalStateException("Unable to find a view to bind this snack too"));
      return null;
    }
    return view;
  }

  //
  // trash
  //

  @Deprecated public static void asToast(Context context, String msg) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
  }

  @Deprecated public static void asToast(Context context, @StringRes int msg) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
  }
}
