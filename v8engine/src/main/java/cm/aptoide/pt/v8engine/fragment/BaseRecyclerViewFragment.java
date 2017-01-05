/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 23/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.LifecycleSchim;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class BaseRecyclerViewFragment<T extends BaseAdapter>
    extends BaseLoaderToolbarFragment implements LifecycleSchim {

  @Getter protected T adapter;
  @Getter protected RecyclerView.LayoutManager layoutManager;
  @Getter protected RecyclerView recyclerView;
  private ArrayList<Displayable> displayables = new ArrayList<>();

  @CallSuper @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    adapter = createAdapter();

    super.onViewCreated(view, savedInstanceState);

    if (adapter != null) {
      adapter.onViewCreated();
    }
  }

  @CallSuper @Override
  public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    if (create || refresh) {
      clearDisplayables();
    } else {
      setDisplayables(new LinkedList<>(displayables));
    }
  }

  @Override public int getContentViewId() {
    return R.layout.recycler_fragment;
  }

  @CallSuper @Override public void setupViews() {
    super.setupViews();
    recyclerView.setAdapter(adapter);
    layoutManager = createLayoutManager();
    recyclerView.setLayoutManager(layoutManager);
  }

  @CallSuper @Override public void onDestroyView() {
    super.onDestroyView();

    // Lifecycle interface
    if (adapter != null) {
      adapter.onDestroyView();
    }

    recyclerView.clearOnScrollListeners();
    recyclerView.setAdapter(null);
    recyclerView = null;
    adapter = null;
  }

  @CallSuper @Override public void bindViews(View view) {
    super.bindViews(view);
    recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
  }

  protected abstract T createAdapter();

  protected abstract RecyclerView.LayoutManager createLayoutManager();

  //
  // Displayables methods
  //

  @CallSuper public void addDisplayable(int position, Displayable displayable) {
    this.displayables.add(position, displayable);
    adapter.addDisplayable(position, displayable);
  }

  public void addDisplayable(Displayable displayable) {
    this.displayables.add(displayable);
    adapter.addDisplayable(displayable);
    finishLoading();
  }

  @CallSuper public void addDisplayables(List<? extends Displayable> displayables) {
    this.displayables.addAll(displayables);
    adapter.addDisplayables(displayables);
    finishLoading();
  }

  @CallSuper public void setDisplayables(List<? extends Displayable> displayables) {
    clearDisplayables();
    addDisplayables(displayables);
  }

  @CallSuper @Deprecated
  public void addDisplayables(int position, List<? extends Displayable> displayables) {
    adapter.addDisplayables(position, displayables);
    finishLoading();
  }

  @CallSuper public void clearDisplayables() {
    this.displayables.clear();
    adapter.clearDisplayables();
  }

  //
  // Lifecycle interface
  //

  /**
   * This method will not call "onResume" in the adapter elements because in the first run despite
   * de adapter is not null it is empty. Further calls to this
   * method will invoke the proper "onRsume" event in the adapters elements.
   */
  @CallSuper @Override public void onResume() {
    super.onResume();
    if (adapter != null) {
      adapter.onResume();
    }
  }

  @CallSuper @Override public void onPause() {
    super.onPause();
    if (adapter != null) {
      adapter.onPause();
    }
  }

  @CallSuper @Override public void onViewCreated() {
    if (adapter != null) {
      adapter.onViewCreated();
    }
  }

  @CallSuper @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (adapter != null) {
      adapter.onViewStateRestored(savedInstanceState);
    }
  }

  @CallSuper @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (adapter != null) {
      adapter.onSaveInstanceState(outState);
    }
  }
}
