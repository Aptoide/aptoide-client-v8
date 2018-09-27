package cm.aptoide.pt.commentdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import javax.inject.Inject;

public class CommentDetailFragment extends NavigationTrackFragment implements CommentDetailView {

  @Inject CommentDetailPresenter presenter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    attachPresenter(presenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_comment_detail, container, false);
  }

  @Override public void showCommentModel(CommentDetailViewModel viewModel) {
    // TODO: 27/09/2018 show comment view model
  }

  @Override public void showLoading() {
    // TODO: 27/09/2018 show loading
  }

  @Override public void hideLoading() {
    // TODO: 27/09/2018 hide loading
  }
}
