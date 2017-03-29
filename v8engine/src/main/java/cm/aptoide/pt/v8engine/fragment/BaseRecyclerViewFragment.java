package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.DisplayableManager;
import cm.aptoide.pt.v8engine.interfaces.LifecycleSchim;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class BaseRecyclerViewFragment<T extends BaseAdapter>
    extends BaseLoaderToolbarFragment implements LifecycleSchim, DisplayableManager {

  private T adapter;
  private RecyclerView.LayoutManager layoutManager;
  private RecyclerView recyclerView;

  // FIXME: 24/1/2017 sithengineer this is an hack to keep state in the fragment. not safe.
  private ArrayList<Displayable> displayables = new ArrayList<>();

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
  }

  @Override public int getContentViewId() {
    return R.layout.recycler_fragment;
  }

  @CallSuper @Override public void setupViews() {
    super.setupViews();
    recyclerView.setAdapter(adapter);
    layoutManager = createLayoutManager();
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setSaveEnabled(true); //Controls whether the saving of this view's state is enabled
  }

  @CallSuper @Override public void bindViews(View view) {
    super.bindViews(view);
    recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
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

  protected abstract RecyclerView.LayoutManager createLayoutManager();

  //
  // Displayables methods
  //
  @CallSuper @Deprecated public void addDisplayable(Displayable displayable) {
    addDisplayable(displayable, true);
  }

  @Partners @CallSuper @Deprecated
  public void addDisplayables(List<? extends Displayable> displayables) {
    addDisplayables(displayables, true);
  }

  @Override @CallSuper
  public BaseRecyclerViewFragment addDisplayable(int position, Displayable displayable,
      boolean finishedLoading) {
    adapter.addDisplayable(position, displayable);
    this.displayables.add(position, displayable);

    if (finishedLoading) {
      finishLoading();
    }
    return this;
  }

  @Override @CallSuper
  public BaseRecyclerViewFragment replaceDisplayable(int position, Displayable displayable,
      boolean finishedLoading) {

    adapter.removeDisplayable(position);
    adapter.addDisplayable(position, displayable);
    adapter.notifyItemChanged(position);

    this.displayables.remove(position);
    this.displayables.add(position, displayable);

    if (finishedLoading) {
      finishLoading();
    }
    return this;
  }

  @Override @CallSuper
  public BaseRecyclerViewFragment addDisplayable(Displayable displayable, boolean finishedLoading) {
    adapter.addDisplayable(displayable);
    this.displayables.add(displayable);

    if (finishedLoading) {
      finishLoading();
    }
    return this;
  }

  @Override @CallSuper
  public BaseRecyclerViewFragment addDisplayables(List<? extends Displayable> displayables,
      boolean finishedLoading) {
    adapter.addDisplayables(displayables);
    this.displayables.addAll(displayables);

    if (finishedLoading) {
      finishLoading();
    }

    return this;
  }

  @Override @Nullable public Displayable getDisplayableAt(int index) {
    if (displayables != null && displayables.size() > index) {
      return displayables.get(0);
    }
    return null;
  }

  @Override @CallSuper public BaseRecyclerViewFragment addDisplayables(int position,
      List<? extends Displayable> displayables, boolean finishedLoading) {
    adapter.addDisplayables(position, displayables);
    this.displayables.addAll(position, displayables);

    if (finishedLoading) {
      finishLoading();
    }
    return this;
  }

  @Override @CallSuper public BaseRecyclerViewFragment clearDisplayables() {
    adapter.clearDisplayables();
    this.displayables.clear();

    return this;
  }

  @Override public boolean hasDisplayables() {
    return displayables != null && displayables.size() > 0;
  }

  @Partners @CallSuper @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    if (adapter == null) {
      adapter = createAdapter();
    }

    super.onViewCreated(view, savedInstanceState);

    this.onViewCreated();
  }

  @Partners @CallSuper @Override
  public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    if (create || refresh) {
      clearDisplayables();
    } else {
      // FIXME: 24/1/2017 sithengineer used to clear and restore displayables. not a good solution
      // create copy
      List<Displayable> displayablesCopy = new LinkedList<>(displayables);
      // clear displayables and adapter displayables
      this.displayables.clear();
      adapter.clearDisplayables();
      // add copied displayables
      this.displayables.addAll(displayablesCopy);
      adapter.addDisplayables(displayablesCopy);
      // trigger finish loading
      finishLoading();
    }
  }

  //
  // Fragment lifecycle events
  //

  protected abstract T createAdapter();

  //
  // Lifecycle interface
  //

  @CallSuper @Override public void onViewCreated() {
    if (adapter != null) {
      adapter.onViewCreated();
    }
  }

  /**
   * This method will not call "onResume" in the adapter elements because, despite
   * de adapter is not null, in the first run it is empty. Further calls to this
   * method will invoke the proper "onResume" event in the adapters elements.
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

  //
  // getters
  //

  public T getAdapter() {
    return adapter;
  }

  public RecyclerView.LayoutManager getLayoutManager() {
    return layoutManager;
  }

  @Partners public RecyclerView getRecyclerView() {
    return recyclerView;
  }
}
