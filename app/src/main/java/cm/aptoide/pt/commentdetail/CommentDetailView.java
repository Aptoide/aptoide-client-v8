package cm.aptoide.pt.commentdetail;

import cm.aptoide.pt.presenter.View;

public interface CommentDetailView extends View {

  void showCommentModel(CommentDetailViewModel viewModel);

  void showLoading();

  void hideLoading();
}

