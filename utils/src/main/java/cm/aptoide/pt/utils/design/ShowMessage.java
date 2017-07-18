/*
 * Copyright (c) 2016.
 * Modified on 25/08/2016.
 */

package cm.aptoide.pt.utils.design;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;
import cm.aptoide.pt.logger.Logger;
import rx.Completable;
import rx.CompletableSubscriber;

/**
 * Created by trinkes on 5/9/16.
 *
 * Use a Snackbar without this syntactic sugar to avoid errors down the road.
 *
 * Using this class with an activity can yield problems if the active component
 * is a view inside a fragment that is only partially visible
 */
@Deprecated public class ShowMessage {

  private static final String TAG = ShowMessage.class.getSimpleName();

  public static void asSnack(View view, String msg, String actionMsg, View.OnClickListener action,
      int duration) {
    asSnackInternal(view, msg, actionMsg, action, duration).show();
  }

  @NonNull private static Snackbar asSnackInternal(View view, String msg, String actionMsg,
      View.OnClickListener action, int duration) {
    return Snackbar.make(view, msg, duration)
        .setAction(actionMsg, action);
  }

  @NonNull public static Completable asObservableSnack(View view, String msg, String actionMsg,
      View.OnClickListener action) {
    return asSnackObservableInternal(
        asSnackInternal(view, msg, actionMsg, action, Snackbar.LENGTH_SHORT));
  }

  private static Completable asSnackObservableInternal(Snackbar snackbar) {

    return Completable.create(new Completable.OnSubscribe() {

      @Override public void call(CompletableSubscriber completableSubscriber) {

        Snackbar.Callback snackbarCallback = new Snackbar.Callback() {
          @Override public void onShown(Snackbar snackbar) {
            super.onShown(snackbar);
            // does nothing
          }

          @Override public void onDismissed(Snackbar snackbar, int event) {
            super.onDismissed(snackbar, event);
            snackbar.removeCallback(this);
            completableSubscriber.onCompleted();
          }
        };
        snackbar.addCallback(snackbarCallback);
        snackbar.show();
      }
    });
  }

  public static void asSnack(View view, String msg) {
    asSnackInternal(view, msg).show();
  }

  @NonNull private static Snackbar asSnackInternal(View view, String msg) {
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
  }

  @NonNull public static Completable asObservableSnack(View view, String msg) {
    return asSnackObservableInternal(asSnackInternal(view, msg));
  }

  public static void asSnack(View view, @StringRes int msg, @StringRes int actionMsg,
      View.OnClickListener action) {
    asSnackInternal(view, msg, actionMsg, action).show();
  }

  private static Snackbar asSnackInternal(View view, @StringRes int msg, @StringRes int actionMsg,
      View.OnClickListener action) {
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
        .setAction(actionMsg, action);
  }

  @NonNull public static Completable asObservableSnack(View view, @StringRes int msg,
      @StringRes int actionMsg, View.OnClickListener action) {
    return asSnackObservableInternal(asSnackInternal(view, msg, actionMsg, action));
  }

  public static void asSnack(View view, @StringRes int msg) {
    asSnackInternal(view, msg).show();
  }

  private static Snackbar asSnackInternal(View view, @StringRes int msg) {
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
  }

  @NonNull public static Completable asObservableSnack(View view, @StringRes int msg) {
    return asSnackObservableInternal(asSnackInternal(view, msg));
  }

  public static void asLongSnack(Activity activity, String msg) {
    asSnackInternal(activity, msg, Snackbar.LENGTH_LONG).show();
  }

  public static void asLongSnack(Fragment fragment, String msg) {
    asLongSnackInternal(fragment, msg).show();
  }

  public static void asLongSnack(Fragment fragment, @StringRes int msg) {
    asLongSnackInternal(fragment, msg).show();
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

  @NonNull public static Completable asObservableSnack(Activity activity, String msg) {
    return asSnackObservableInternal(asSnackInternal(activity, msg));
  }

  public static void asSnack(Activity activity, @StringRes int msg) {
    asSnackInternal(activity, msg).show();
  }

  private static Snackbar asSnackInternal(Activity activity, @StringRes int msg) {
    View view = getViewFromActivity(activity);
    return Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
  }

  @NonNull public static Completable asObservableSnack(Activity activity, @StringRes int msg) {
    return asSnackObservableInternal(asSnackInternal(activity, msg));
  }

  @NonNull public static Completable asLongObservableSnack(Activity activity, @StringRes int msg) {
    return asSnackObservableInternal(asLongSnackInternal(activity, msg));
  }

  @NonNull public static Completable asLongObservableSnack(View view, @StringRes int msg) {
    return asSnackObservableInternal(asLongSnackInternal(view, msg));
  }

  @NonNull public static Completable asLongObservableSnack(View view, String msg) {
    return asSnackObservableInternal(asLongSnackInternal(view, msg));
  }

  @NonNull public static Completable asLongObservableSnack(Fragment fragment, @StringRes int msg) {
    return asSnackObservableInternal(asLongSnackInternal(fragment, msg));
  }

  private static Snackbar asLongSnackInternal(View view, String msg) {
    return Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
  }

  private static Snackbar asLongSnackInternal(View view, @StringRes int msg) {
    return Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
  }

  private static Snackbar asLongSnackInternal(Fragment fragment, @StringRes int msg) {
    return asLongSnackInternal(fragment.getView(), msg);
  }

  private static Snackbar asLongSnackInternal(Fragment fragment, String msg) {
    return asLongSnackInternal(fragment.getView(), msg);
  }

  private static Snackbar asLongSnackInternal(Activity activity, @StringRes int msg) {
    return asLongSnackInternal(getViewFromActivity(activity), msg);
  }

  public static void asSnack(Fragment fragment, String msg) {
    asSnackInternal(fragment, msg).show();
  }

  @NonNull private static Snackbar asSnackInternal(Fragment fragment, String msg) {
    return Snackbar.make(fragment.getView(), msg, Snackbar.LENGTH_SHORT);
  }

  @NonNull public static Completable asObservableSnack(Fragment fragment, String msg) {
    return asSnackObservableInternal(asSnackInternal(fragment, msg));
  }

  public static void asSnack(Fragment fragment, @StringRes int msg) {
    asSnackInternal(fragment, msg).show();
  }

  @NonNull private static Snackbar asSnackInternal(Fragment fragment, @StringRes int msg) {
    return Snackbar.make(fragment.getView(), msg, Snackbar.LENGTH_SHORT);
  }

  @NonNull public static Completable asObservableSnack(Fragment fragment, @StringRes int msg) {
    return asSnackObservableInternal(asSnackInternal(fragment, msg));
  }

  public static void asSnack(Activity activity, int msg, int actionMsg, View.OnClickListener action,
      int duration) {
    Snackbar snackbar = asSnackInternal(activity, msg, actionMsg, action, duration);
    if (snackbar != null) {
      snackbar.show();
    }
  }

  public static void asSnackIndefiniteTime(Activity activity, String msg, int actionMsg,
      View.OnClickListener action) {
    Snackbar snackbar =
        asSnackInternal(activity, msg, actionMsg, action, Snackbar.LENGTH_INDEFINITE);
    if (snackbar != null) {
      snackbar.show();
    }
  }

  private static Snackbar asSnackInternal(Activity activity, String msg, int actionMsg,
      View.OnClickListener action, int duration) {
    View view = getViewFromActivity(activity);
    if (view == null) {
      return null;
    }
    return Snackbar.make(view, msg, duration)
        .setAction(actionMsg, action);
  }

  private static Snackbar asSnackInternal(Activity activity, int msg, int actionMsg,
      View.OnClickListener action, int duration) {
    View view = getViewFromActivity(activity);
    if (view == null) {
      return null;
    }
    return Snackbar.make(view, msg, duration)
        .setAction(actionMsg, action);
  }

  @NonNull public static Completable asObservableSnack(Activity activity, int msg, int actionMsg,
      View.OnClickListener action) {
    Snackbar snackbar = asSnackInternal(activity, msg, actionMsg, action, Snackbar.LENGTH_SHORT);
    if (snackbar != null) {
      return asSnackObservableInternal(snackbar);
    }
    return Completable.error(new IllegalStateException("Extracted view from activity is null"));
  }

  public static void asSnack(android.app.Fragment fragment, @StringRes int msg) {
    asSnackInternal(fragment, msg).show();
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) @NonNull
  private static Snackbar asSnackInternal(android.app.Fragment fragment, @StringRes int msg) {
    return Snackbar.make(fragment.getView(), msg, Snackbar.LENGTH_SHORT);
  }

  @NonNull
  public static Completable asObservableSnack(android.app.Fragment fragment, @StringRes int msg) {
    return asSnackObservableInternal(asSnackInternal(fragment, msg));
  }

  @Deprecated public static void asToast(Context context, String msg) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        .show();
  }

  @Deprecated public static void asToast(Context context, @StringRes int msg) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        .show();
  }
}
