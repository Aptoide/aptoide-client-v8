package cm.aptoide.pt.viewRateAndCommentReviews;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.PostCommentForReviewRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.PostCommentForStore;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.RxDialogFragment;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class CommentDialogFragment extends RxDialogFragment {

  private static final String TAG = CommentDialogFragment.class.getName();

  private static final String APP_OR_STORE_NAME = "app_or_store_name";
  private static final String RESOURCE_ID = "resource_id";
  private static final String IS_REVIEW = "is_review";
  private static final String PREVIOUS_COMMENT_ID = "previous_comment_id";

  private String appOrStoreName;
  private long id;
  private boolean isReview;
  private Long previousCommentId;

  private TextInputLayout textInputLayout;
  private Button commentButton;
  private final String onEmptyTextError;

  public static CommentDialogFragment newInstanceStoreCommentReply(long storeId,
      long previousCommentId, String storeName) {
    Bundle args = new Bundle();
    args.putString(APP_OR_STORE_NAME, TextUtils.isEmpty(storeName) ? null : storeName);
    args.putBoolean(IS_REVIEW, false);
    args.putLong(RESOURCE_ID, storeId);
    args.putLong(PREVIOUS_COMMENT_ID, previousCommentId);

    CommentDialogFragment fragment = new CommentDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static CommentDialogFragment newInstanceReview(long id, String appName) {
    Bundle args = new Bundle();
    args.putString(APP_OR_STORE_NAME, appName);
    args.putBoolean(IS_REVIEW, true);
    args.putLong(RESOURCE_ID, id);

    CommentDialogFragment fragment = new CommentDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static CommentDialogFragment newInstanceStoreComment(long id, String storeName) {
    Bundle args = new Bundle();
    args.putString(APP_OR_STORE_NAME, storeName);
    args.putBoolean(IS_REVIEW, false);
    args.putLong(RESOURCE_ID, id);

    CommentDialogFragment fragment = new CommentDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public CommentDialogFragment() {
    onEmptyTextError = AptoideUtils.StringU.getResString(R.string.error_MARG_107);
  }

  private void loadArguments() {
    Bundle args = getArguments();
    this.appOrStoreName = args.getString(APP_OR_STORE_NAME);
    this.isReview = args.getBoolean(IS_REVIEW);
    this.id = args.getLong(RESOURCE_ID);

    if (args.containsKey(PREVIOUS_COMMENT_ID)) {
      this.previousCommentId = args.getLong(PREVIOUS_COMMENT_ID);
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    loadArguments();

    View view = inflater.inflate(R.layout.dialog_comment_on_review, container);

    TextView titleTextView = (TextView) view.findViewById(R.id.title);
    titleTextView.setVisibility(View.VISIBLE);

    titleTextView.setText(String.format(getString(R.string.comment_on_store),
        TextUtils.isEmpty(appOrStoreName) ? getString(R.string.word_this) : appOrStoreName));

    Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
    cancelButton.setOnClickListener(a -> CommentDialogFragment.this.dismiss());

    textInputLayout = (TextInputLayout) view.findViewById(R.id.input_layout_title);
    commentButton = (Button) view.findViewById(R.id.comment_button);

    setupLogic();

    return view;
  }

  //
  // logic
  //

  private String getText() {
    if (textInputLayout != null) {
      return textInputLayout.getEditText().getEditableText().toString();
    }
    return null;
  }

  private void enableError(String error) {
    textInputLayout.setError(error);
  }

  private void disableError() {
    textInputLayout.setErrorEnabled(false);
  }

  private void setupLogic() {

    textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
        if (charSequence.length() > 0) {
          disableError();
        }
      }

      @Override public void afterTextChanged(Editable editable) {
      }
    });

    RxView.clicks(commentButton)
        .flatMap(a -> Observable.just(getText()))
        .filter(inputText -> {
          if (TextUtils.isEmpty(inputText)) {
            enableError(onEmptyTextError);
            return false;
          }
          disableError();
          return true;
        })
        .flatMap(inputText -> submitComment(inputText).observeOn(AndroidSchedulers.mainThread()))
        .map(wsRespose -> wsRespose.isOk())
        .doOnError(e -> {
          Logger.e(TAG, e);
          ShowMessage.asSnack(CommentDialogFragment.this, R.string.error_occured);
        })
        .retry()
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(isOk -> {
          if (isOk) {
            ManagerPreferences.setForceServerRefreshFlag(true);
            this.dismiss();
            ShowMessage.asSnack(this.getActivity(), R.string.comment_submitted);
            return;
          }
          ShowMessage.asSnack(CommentDialogFragment.this, R.string.error_occured);
        });
  }

  private Observable<BaseV7Response> submitComment(String inputText) {

    if (isReview) {
      return PostCommentForReviewRequest.of(id, inputText, AptoideAccountManager.getAccessToken(),
          new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
              DataProvider.getContext()).getAptoideClientUUID()).observe();
    }

    // check if this is a new comment on a store or a reply to a previous one
    if (previousCommentId == null) {
      return PostCommentForStore.of(id, inputText, AptoideAccountManager.getAccessToken(),
          new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
              DataProvider.getContext()).getAptoideClientUUID()).observe();
    }

    return PostCommentForStore.of(id, previousCommentId, inputText,
        AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID()).observe();
  }
}
