package cm.aptoide.pt.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import java.util.List;
import javax.inject.Inject;

public class CommentsFragment extends NavigationTrackFragment implements CommentsView {

  @Inject CommentsPresenter commentsPresenter;

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
    Logger.getInstance()
        .d(this.getTag(), "comments fragment");

    attachPresenter(commentsPresenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void showComments(List<String> comments) {
    // TODO: 21/09/2018 actual comments shown
  }

  @Override public void showLoading() {
    // TODO: 21/09/2018 show loading
  }

  @Override public void hideLoading() {
    // TODO: 21/09/2018 hide loading
  }
}
