package cm.aptoide.pt.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class CommentsFragment extends NavigationTrackFragment implements CommentsView {

  @Inject CommentsPresenter commentsPresenter;
  @Inject AptoideUtils.DateTimeU dateUtils;
  private RecyclerView commentsList;
  private CommentsAdapter commentsAdapter;
  private View loading;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_comments, container, false);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    loading = view.findViewById(R.id.progress_bar);
    commentsList = view.findViewById(R.id.comments_list);
    commentsList.setLayoutManager(
        new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
    commentsAdapter = new CommentsAdapter(dateUtils, Collections.emptyList());
    commentsList.setAdapter(commentsAdapter);

    attachPresenter(commentsPresenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void showComments(List<Comment> comments) {
    commentsAdapter.addComments(comments);
  }

  @Override public void showLoading() {
    commentsList.setVisibility(View.GONE);
    loading.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    commentsList.setVisibility(View.VISIBLE);
    loading.setVisibility(View.GONE);
  }

  @Override public void showGeneralError() {
    // TODO: 24/09/2018 show error
    Toast.makeText(this.getContext(), "ERROR", Toast.LENGTH_SHORT)
        .show();
  }
}
