package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreLatestCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import java.util.ArrayList;
import java.util.List;

public class StoreLatestCommentsWidget extends Widget<StoreLatestCommentsDisplayable> {

  private RecyclerView recyclerView;

  public StoreLatestCommentsWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    recyclerView = (RecyclerView) itemView.findViewById(R.id.comments);
  }

  @Override public void bindView(StoreLatestCommentsDisplayable displayable) {
    LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(layoutManager);

    recyclerView.setAdapter(new CommentListAdapter(displayable.getComments()));
  }

  private static class CommentListAdapter extends BaseAdapter {

    CommentListAdapter(List<Comment> comments) {
      super();
      ArrayList<Displayable> displayables = new ArrayList<>(comments.size());
      for (Comment comment : comments) {
        displayables.add(new CommentDisplayable(comment));
      }
      addDisplayables(displayables);
    }
  }
}
