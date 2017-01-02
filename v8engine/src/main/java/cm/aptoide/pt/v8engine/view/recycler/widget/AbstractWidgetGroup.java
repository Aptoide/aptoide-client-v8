package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseGridLayoutManager;
import cm.aptoide.pt.v8engine.view.recycler.displayable.AbstractDisplayableGroup;

/**
 * Created by neuro on 28-12-2016.
 */

public abstract class AbstractWidgetGroup<T extends AbstractDisplayableGroup> extends Widget<T> {

  private RecyclerView recyclerView;

  public AbstractWidgetGroup(@NonNull View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
    RecyclerView.ItemDecoration recyclerViewDecorator = getRecyclerViewDecorator();
    if (recyclerViewDecorator != null) {
      recyclerView.addItemDecoration(recyclerViewDecorator);
    }
  }

  @Override public void bindView(T displayable) {
    BaseAdapter adapter = new BaseAdapter(displayable.getChildren());
    recyclerView.setNestedScrollingEnabled(false);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(getLayoutManager(adapter));
  }

  protected abstract RecyclerView.ItemDecoration getRecyclerViewDecorator();

  @NonNull protected RecyclerView.LayoutManager getLayoutManager(BaseAdapter adapter) {
    return new BaseGridLayoutManager(getContext(), adapter);
  }
}
