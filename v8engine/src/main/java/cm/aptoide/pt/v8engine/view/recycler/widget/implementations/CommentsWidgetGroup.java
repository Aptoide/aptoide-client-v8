package cm.aptoide.pt.v8engine.view.recycler.widget.implementations;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.v8engine.view.custom.HorizontalDividerItemDecoration;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.CommentsDisplayableGroup;
import cm.aptoide.pt.v8engine.view.recycler.widget.AbstractWidgetGroup;
import java.util.Collections;
import java.util.List;

/**
 * Created by neuro on 28-12-2016.
 */

public class CommentsWidgetGroup extends AbstractWidgetGroup<CommentsDisplayableGroup> {

  public CommentsWidgetGroup(@NonNull View itemView) {
    super(itemView);
  }

  @Override protected List<RecyclerView.ItemDecoration> createRecyclerViewDecorators() {
    return Collections.singletonList(new HorizontalDividerItemDecoration(getContext()));
  }
}
