package cm.aptoide.pt.v8engine.view.recycler;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.view.LifecycleSchim;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetFactory;
import java.util.List;

/**
 * Created by neuro on 16-04-2016.
 */
public class BaseAdapter extends RecyclerView.Adapter<Widget> implements LifecycleSchim {

  private final Displayables displayables;

  public BaseAdapter(List<Displayable> displayables) {
    this();
    this.displayables.add(displayables);
  }

  public BaseAdapter() {
    displayables = new Displayables();
  }

  @Override public Widget onCreateViewHolder(ViewGroup parent, int viewType) {
    return WidgetFactory.newBaseViewHolder(parent, viewType);
  }

  @SuppressWarnings("unchecked") @Override
  public void onBindViewHolder(Widget holder, int position) {
    holder.internalBindView(displayables.get(position));
  }

  @Override public int getItemViewType(int position) {
    return displayables.get(position)
        .getViewLayout();
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public int getItemCount() {
    return displayables.size();
  }

  @Override public void onViewRecycled(Widget holder) {
    holder.unbindView();
    super.onViewRecycled(holder);
  }

  public Displayable popDisplayable() {
    //return Single.fromCallable(() -> {
    //  Displayable pop = displayables.pop();
    //  notifyItemRemoved(displayables.size());
    //  return pop;
    //}).toBlocking().value();

    Displayable pop = displayables.pop();
    notifyItemRemoved(displayables.size());
    return pop;
  }

  public Displayable getDisplayable(int position) {
    return this.displayables.get(position);
  }

  public void addDisplayable(int position, Displayable displayable) {
    AptoideUtils.ThreadU.runOnUiThread(() -> {
      this.displayables.add(position, displayable);
      notifyDataSetChanged();
    });
  }

  public void addDisplayable(Displayable displayable) {
    AptoideUtils.ThreadU.runOnUiThread(() -> {
      this.displayables.add(displayable);
      notifyItemInserted(this.displayables.size() - 1);
    });
  }

  public void addDisplayables(List<? extends Displayable> displayables) {
    AptoideUtils.ThreadU.runOnUiThread(() -> {
      this.displayables.add(displayables);
      notifyDataSetChanged();
    });
  }

  public void addDisplayables(int position, List<? extends Displayable> displayables) {
    AptoideUtils.ThreadU.runOnUiThread(() -> {
      this.displayables.add(position, displayables);
      notifyItemRangeInserted(position, displayables.size());
    });
  }

  public void removeDisplayables(int startPosition, int endPosition) {
    AptoideUtils.ThreadU.runOnUiThread(() -> {
      int numberRemovedItems = this.displayables.remove(startPosition, endPosition);
      notifyItemRangeRemoved(startPosition, numberRemovedItems);
    });
  }

  public void removeDisplayable(int position) {
    AptoideUtils.ThreadU.runOnUiThread(() -> {
      this.displayables.remove(position);
      notifyItemRemoved(position);
    });
  }

  public void clearDisplayables() {
    AptoideUtils.ThreadU.runOnUiThread(() -> {
      displayables.clear();
      notifyDataSetChanged();
    });
  }

  //
  // LifecycleShim interface
  //

  public void onResume() {
    displayables.onResume();
  }

  public void onPause() {
    displayables.onPause();
  }

  @Override public void onViewCreated() {
    displayables.onViewCreated();
  }

  @Override public void onDestroyView() {
    displayables.onDestroyView();
  }

  public void onSaveInstanceState(Bundle outState) {
    displayables.onSaveInstanceState(outState);
  }

  public void onViewStateRestored(Bundle savedInstanceState) {
    displayables.onViewStateRestored(savedInstanceState);
  }

  public void removeDisplayable(Displayable displayable) {
    displayables.remove(displayable);
    notifyItemRemoved(displayables.getPosition(displayable));
  }
}
