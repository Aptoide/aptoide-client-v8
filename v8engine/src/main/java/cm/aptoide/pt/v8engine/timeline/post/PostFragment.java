package cm.aptoide.pt.v8engine.timeline.post;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import rx.Completable;
import rx.Observable;

public class PostFragment extends FragmentView implements PostView {

  private static final String DATA_TO_SHARE = "data_to_share";
  private ProgressBar progressBar;
  private EditText userInput;
  private Button share;

  public static PostFragment newInstance(String toShare) {
    Bundle args = new Bundle();
    args.putString(DATA_TO_SHARE, toShare);

    PostFragment fragment = new PostFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(getContentViewId(), container, false);
    bindViews(root);
    setupViews();
    return root;
  }

  @LayoutRes private int getContentViewId() {
    return R.layout.fragment_post;
  }

  private void bindViews(@Nullable View view) {
    userInput = (EditText) view.findViewById(R.id.input_text);
    share = (Button) view.findViewById(R.id.share_button);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
  }

  @Override public void onDestroyView() {
    if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
      progressBar.setVisibility(View.GONE);
      progressBar = null;
    }

    super.onDestroyView();
  }

  private void setupViews() {
    Bundle args = getArguments();
    if (args != null) {
      userInput.setText(args.getString(DATA_TO_SHARE, ""));
    }

    attachPresenter(new PostPresenter(this, CrashReport.getInstance(), new PostManager()), null);
  }

  @Override public Observable<String> onInputTextChanged() {
    return RxTextView.textChanges(userInput)
        .map(data -> data.toString());
  }

  @Override public Observable<String> shareButtonPressed() {
    return RxView.clicks(share)
        .map(__ -> getInputText());
  }

  @Override public Completable close() {
    return Completable.fromAction(() -> PostFragment.this.getActivity()
        .onBackPressed());
  }

  @Override public Completable hideLoading() {
    return Completable.fromAction(() -> progressBar.setVisibility(View.GONE));
  }

  @Override public Completable showSuccessMessage() {
    return ShowMessage.asLongObservableSnack(getActivity(), R.string.title_successful)
        .toCompletable();
  }

  @Override public Completable showLoading() {
    return Completable.fromAction(() -> progressBar.setVisibility(View.VISIBLE));
  }

  @Override public Completable showSuggestion(String suggestion) {
    return Completable.fromAction(() -> Logger.i("PostFragment", "suggestion: " + suggestion));
  }

  private String getInputText() {
    return userInput.getText()
        .toString();
  }
}
