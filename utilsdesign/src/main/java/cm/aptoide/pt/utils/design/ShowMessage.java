/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/08/2016.
 */

package cm.aptoide.pt.utils.design;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
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

  public static final int VISIBLE = 0;

  //
  // override 1
  //
  public static final int DISMISSED = 1;
  private static final String TAG = ShowMessage.class.getSimpleName();

  public static void asSnack(View view, String msg, String actionMsg, View.OnClickListener action) {
    asSnackInternal(view, msg, actionMsg, action).show();
  }

  //
  // override 2
  //

  @NonNull private static Snackbar asSnackInternal(View view, String msg, String actionMsg,
      View.OnClickListener action) {
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setAction(actionMsg, action);
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(View view, String msg,
      String actionMsg, View.OnClickListener action) {
    return asSnackObservableInternal(asSnackInternal(view, msg, actionMsg, action));
  }

  private static Observable<Integer> asSnackObservableInternal(Snackbar snackbar) {

    return Observable.create(new Observable.OnSubscribe<Integer>() {
      @Override public void call(Subscriber<? super Integer> subscriber) {
        Snackbar.Callback snackbarCallback = new Snackbar.Callback() {
          @Override public void onShown(Snackbar snackbar) {
            super.onShown(snackbar);
            if (!subscriber.isUnsubscribed()) {
              subscriber.onNext(VISIBLE);
            }
          }

          @Override public void onDismissed(Snackbar snackbar, int event) {
            super.onDismissed(snackbar, event);
            if (!subscriber.isUnsubscribed()) {
              subscriber.onNext(DISMISSED);
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

  //
  // override 3
  //

  public static void asSnack(View view, String msg) {
    asSnackInternal(view, msg).show();
  }

  @NonNull private static Snackbar asSnackInternal(View view, String msg) {
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(View view, String msg) {
    return asSnackObservableInternal(asSnackInternal(view, msg));
  }

  //
  // override 4
  //

  public static void asSnack(View view, @StringRes int msg, @StringRes int actionMsg,
      View.OnClickListener action) {
    asSnackInternal(view, msg, actionMsg, action).show();
  }

  private static Snackbar asSnackInternal(View view, @StringRes int msg, @StringRes int actionMsg,
      View.OnClickListener action) {
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setAction(actionMsg, action);
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(View view, @StringRes int msg,
      @StringRes int actionMsg, View.OnClickListener action) {
    return asSnackObservableInternal(asSnackInternal(view, msg, actionMsg, action));
  }

  //
  // override 5
  //

  public static void asSnack(View view, @StringRes int msg) {
    asSnackInternal(view, msg).show();
  }

  private static Snackbar asSnackInternal(View view, @StringRes int msg) {
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(View view, @StringRes int msg) {
    return asSnackObservableInternal(asSnackInternal(view, msg));
  }

  //
  // override 6
  //

  public static void asLongSnack(Activity activity, String msg) {
    asSnackInternal(activity, msg, Snackbar.LENGTH_LONG).show();
  }

  @NonNull private static Snackbar asSnackInternal(Activity activity, String msg, int duration) {
    View view = getViewFromActivity(activity);
    return Snackbar.make(view, msg, duration);
  }

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

  public static void asSnack(Activity activity, String msg) {
    asSnackInternal(activity, msg).show();
  }

  @NonNull private static Snackbar asSnackInternal(Activity activity, String msg) {
    return asSnackInternal(activity, msg, Snackbar.LENGTH_SHORT);
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(Activity activity, String msg) {
    return asSnackObservableInternal(asSnackInternal(activity, msg));
  }

  public static void asSnack(Activity activity, @StringRes int msg) {
    asSnackInternal(activity, msg).show();
  }

  //
  // override 7
  //

  private static Snackbar asSnackInternal(Activity activity, @StringRes int msg) {
    View view = getViewFromActivity(activity);
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(Activity activity,
      @StringRes int msg) {
    return asSnackObservableInternal(asSnackInternal(activity, msg));
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asLongObservableSnack(Activity activity,
      @StringRes int msg) {
    return asSnackObservableInternal(asLongSnackInternal(activity, msg));
  }

  //
  // override 8
  //

  private static Snackbar asLongSnackInternal(Activity activity, @StringRes int msg) {
    View view = getViewFromActivity(activity);
    return Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
  }

  public static void asSnack(Fragment fragment, String msg) {
    asSnackInternal(fragment, msg).show();
  }

  @NonNull private static Snackbar asSnackInternal(Fragment fragment, String msg) {
    return Snackbar.make(fragment.getView(), msg, Snackbar.LENGTH_SHORT);
  }

  //
  // override 9
  //

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(Fragment fragment, String msg) {
    return asSnackObservableInternal(asSnackInternal(fragment, msg));
  }

  public static void asSnack(Fragment fragment, @StringRes int msg) {
    asSnackInternal(fragment, msg).show();
  }

  @NonNull private static Snackbar asSnackInternal(Fragment fragment, @StringRes int msg) {
    return Snackbar.make(fragment.getView(), msg, Snackbar.LENGTH_SHORT);
  }

  //
  // override 10
  //

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(Fragment fragment,
      @StringRes int msg) {
    return asSnackObservableInternal(asSnackInternal(fragment, msg));
  }

  public static void asSnack(Activity activity, int msg, int actionMsg,
      View.OnClickListener action) {
    Snackbar snackbar = asSnackInternal(activity, msg, actionMsg, action);
    if (snackbar != null) {
      snackbar.show();
    }
  }

  private static Snackbar asSnackInternal(Activity activity, int msg, int actionMsg,
      View.OnClickListener action) {
    View view = getViewFromActivity(activity);
    if (view == null) {
      return null;
    }
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setAction(actionMsg, action);
  }

  //
  // base observable Snackbar
  //

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

  public static void asSnack(android.app.Fragment fragment, @StringRes int msg) {
    asSnackInternal(fragment, msg).show();
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) @NonNull
  private static Snackbar asSnackInternal(android.app.Fragment fragment, @StringRes int msg) {
    return Snackbar.make(fragment.getView(), msg, Snackbar.LENGTH_SHORT);
  }

  /**
   * @return {@link Observable} that returns a {@link ShowMessage.SnackbarVisibility} integer
   */
  @NonNull public static Observable<Integer> asObservableSnack(android.app.Fragment fragment,
      @StringRes int msg) {
    return asSnackObservableInternal(asSnackInternal(fragment, msg));
  }

  @Deprecated public static void asToast(Context context, String msg) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
  }

  //
  // trash
  //

  @Deprecated public static void asToast(Context context, @StringRes int msg) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
  }

  @Retention(SOURCE) @IntDef({ VISIBLE, DISMISSED }) public @interface SnackbarVisibility {
  }
}
