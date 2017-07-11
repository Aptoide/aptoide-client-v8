/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 31/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.v8engine.NavigationProvider;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.ActivityView;
import cm.aptoide.pt.v8engine.view.navigator.ActivityNavigator;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import rx.subscriptions.CompositeSubscription;

/**
 * Class that represents a generic Widget. All widgets should extend this class.
 */
public abstract class Widget<T extends Displayable> extends RecyclerView.ViewHolder {

  private final FragmentNavigator fragmentNavigator;
  protected CompositeSubscription compositeSubscription;
  private ActivityNavigator activityNavigator;

  public Widget(@NonNull View itemView) {
    super(itemView);
    fragmentNavigator = ((NavigationProvider) getContext()).getFragmentNavigator();
    activityNavigator = ((NavigationProvider) getContext()).getActivityNavigator();

    try {
      assignViews(itemView);
    } catch (Exception e) {
      CrashReport.getInstance()
          .log(e);
    }
  }

  public FragmentActivity getContext() {
    return (FragmentActivity) itemView.getContext();
  }

  protected abstract void assignViews(View itemView);

  @CallSuper public void unbindView() {
    if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
      compositeSubscription.clear();
    }
  }

  public void internalBindView(T displayable) {
    if (compositeSubscription == null) {
      compositeSubscription = new CompositeSubscription();
    }
    displayable.setVisible(true);
    bindView(displayable);
  }

  public abstract void bindView(T displayable);

  public View getRootView() {
    return getFragmentNavigator().peekLast()
        .getView();
  }

  protected FragmentNavigator getFragmentNavigator() {
    return fragmentNavigator;
  }

  protected ActivityNavigator getActivityNavigator() {
    return activityNavigator;
  }
}
