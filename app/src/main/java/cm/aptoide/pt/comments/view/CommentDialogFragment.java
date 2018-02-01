package cm.aptoide.pt.comments.view;

import android.content.SharedPreferences;
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
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.comments.CommentBeforeSubmissionCallback;
import cm.aptoide.pt.comments.CommentDialogCallbackContract;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.PostCommentForReview;
import cm.aptoide.pt.dataprovider.ws.v7.PostCommentForTimelineArticle;
import cm.aptoide.pt.dataprovider.ws.v7.store.PostCommentForStore;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import com.facebook.appevents.AppEventsLogger;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class CommentDialogFragment
    extends com.trello.rxlifecycle.components.support.RxDialogFragment {

  private static final String TAG = CommentDialogFragment.class.getName();

  private static final String APP_OR_STORE_NAME = "app_or_store_name";
  private static final String RESOURCE_ID_AS_LONG = "resource_id_as_long";
  private static final String RESOURCE_ID_AS_STRING = "resource_id_as_string";
  private static final String COMMENT_TYPE = "comment_type";
  private static final String PREVIOUS_COMMENT_ID = "previous_comment_id";
  private static final String POSITION = "position";
  private static final String CARD_TYPE = "card_type";
  private static final String SOURCE = "source";
  private static final String APP = "app";
  private static final String URL = "url";
  private String onEmptyTextError;
  private String appOrStoreName;
  private long idAsLong;
  private String idAsString;
  private CommentType commentType;
  private Long previousCommentId;
  private TextInputLayout textInputLayout;
  private Button commentButton;
  private boolean reply;
  private CommentDialogCallbackContract commentDialogCallbackContract;
  private CommentBeforeSubmissionCallback commentBeforeSubmissionCallback;
  private BodyInterceptor<BaseBody> baseBodyBodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;
  private StoreAnalytics storeAnalytics;
  private TimelineAnalytics timelineAnalytics;

  public static CommentDialogFragment newInstanceStoreCommentReply(long storeId,
      long previousCommentId, String storeName) {
    Bundle args = new Bundle();
    args.putString(COMMENT_TYPE, CommentType.STORE.name());
    args.putLong(RESOURCE_ID_AS_LONG, storeId);
    args.putLong(PREVIOUS_COMMENT_ID, previousCommentId);

    if (!TextUtils.isEmpty(storeName)) {
      args.putString(APP_OR_STORE_NAME, storeName);
    }

    CommentDialogFragment fragment = new CommentDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static CommentDialogFragment newInstanceReview(long id, String appName) {
    Bundle args = new Bundle();
    args.putString(COMMENT_TYPE, CommentType.REVIEW.name());
    args.putLong(RESOURCE_ID_AS_LONG, id);

    if (!TextUtils.isEmpty(appName)) {
      args.putString(APP_OR_STORE_NAME, appName);
    }

    CommentDialogFragment fragment = new CommentDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static CommentDialogFragment newInstanceStoreComment(long storeId, String storeName) {
    Bundle args = new Bundle();
    args.putString(COMMENT_TYPE, CommentType.STORE.name());
    args.putLong(RESOURCE_ID_AS_LONG, storeId);

    if (!TextUtils.isEmpty(storeName)) {
      args.putString(APP_OR_STORE_NAME, storeName);
    }

    CommentDialogFragment fragment = new CommentDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static CommentDialogFragment newInstanceTimelineArticleComment(String timelineArticleId,
      int position, String type, String source, String app, String url) {
    Bundle args = new Bundle();
    args.putString(COMMENT_TYPE, CommentType.TIMELINE.name());
    args.putString(RESOURCE_ID_AS_STRING, timelineArticleId);
    args.putInt(POSITION, position);
    args.putString(CARD_TYPE, type);
    args.putString(SOURCE, source);
    args.putString(APP, app);
    args.putString(URL, url);
    CommentDialogFragment fragment = new CommentDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static CommentDialogFragment newInstanceTimelineArticleComment(String timelineArticleId,
      int position, String type, String source, String app, String url, long previousCommentId) {
    Bundle args = new Bundle();
    args.putString(COMMENT_TYPE, CommentType.TIMELINE.name());
    args.putString(RESOURCE_ID_AS_STRING, timelineArticleId);
    args.putLong(PREVIOUS_COMMENT_ID, previousCommentId);
    args.putInt(POSITION, position);
    args.putString(CARD_TYPE, type);
    args.putString(SOURCE, source);
    args.putString(APP, app);
    args.putString(URL, url);
    CommentDialogFragment fragment = new CommentDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AptoideApplication application = (AptoideApplication) getContext().getApplicationContext();
    sharedPreferences = application.getDefaultSharedPreferences();
    tokenInvalidator = application.getTokenInvalidator();
    baseBodyBodyInterceptor = application.getAccountSettingsBodyInterceptorPoolV7();
    httpClient = application.getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    onEmptyTextError =
        AptoideUtils.StringU.getResString(R.string.ws_error_MARG_107, getContext().getResources());
    timelineAnalytics =
        ((AptoideApplication) getContext().getApplicationContext()).getTimelineAnalytics();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    loadArguments();

    View view = inflater.inflate(R.layout.dialog_comment_on_review, container);

    TextView titleTextView = (TextView) view.findViewById(R.id.title);
    titleTextView.setVisibility(View.VISIBLE);

    switch (commentType) {
      case REVIEW:
        titleTextView.setText(getString(R.string.dialog_title_comment));
        break;
      case TIMELINE:
        titleTextView.setText(getString(R.string.dialog_title_comment));
        break;
      case STORE:
        titleTextView.setText(
            TextUtils.isEmpty(appOrStoreName) ? getString(R.string.comment_store_title)
                : getString(R.string.commentlist_title_comment_on_store, appOrStoreName));
        break;
    }

    Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
    Bundle args = getArguments();
    cancelButton.setOnClickListener(a -> {
      logAnalytics(false, args.getInt(POSITION), args.getString(CARD_TYPE), args.getString(SOURCE),
          args.getString(APP), args.getString(URL));
      CommentDialogFragment.this.dismiss();
    });

    textInputLayout = (TextInputLayout) view.findViewById(R.id.input_layout_title);
    commentButton = (Button) view.findViewById(R.id.comment_button);

    setupLogic();
    storeAnalytics =
        new StoreAnalytics(AppEventsLogger.newLogger(getContext().getApplicationContext()),
            Analytics.getInstance());

    return view;
  }

  private void loadArguments() {
    Bundle args = getArguments();
    this.appOrStoreName = args.getString(APP_OR_STORE_NAME, "");
    this.commentType = CommentType.valueOf(args.getString(COMMENT_TYPE));
    this.idAsString = args.getString(RESOURCE_ID_AS_STRING);
    this.idAsLong = args.getLong(RESOURCE_ID_AS_LONG);

    this.reply = args.containsKey(PREVIOUS_COMMENT_ID);
    if (this.reply) {
      this.previousCommentId = args.getLong(PREVIOUS_COMMENT_ID);
    }
  }

  //
  // logic
  //

  private void setupLogic() {
    Bundle args = getArguments();

    textInputLayout.getEditText()
        .addTextChangedListener(new TextWatcher() {
          @Override public void beforeTextChanged(CharSequence charSequence, int start, int count,
              int after) {
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
            logAnalytics(false, args.getInt(POSITION), args.getString(CARD_TYPE),
                args.getString(SOURCE), args.getString(APP), args.getString(URL));
            return false;
          }
          disableError();
          return true;
        })
        .flatMap(inputText -> {
          if (commentBeforeSubmissionCallback != null) {
            commentBeforeSubmissionCallback.onCommentBeforeSubmission(inputText);
            logAnalytics(true, args.getInt(POSITION), args.getString(CARD_TYPE),
                args.getString(SOURCE), args.getString(APP), args.getString(URL));
            this.dismiss();
            return Observable.empty();
          }
          return submitComment(inputText, idAsLong, previousCommentId, idAsString).observeOn(
              AndroidSchedulers.mainThread())
              .doOnError(e -> {
                CrashReport.getInstance()
                    .log(e);
                logAnalytics(false, args.getInt(POSITION), args.getString(CARD_TYPE),
                    args.getString(SOURCE), args.getString(APP), args.getString(URL));
                ShowMessage.asSnack(this, R.string.error_occured);
              })
              .retry()
              .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW));
        })
        .subscribe(resp -> {
          if (resp.isOk()) {
            this.dismiss();
            logAnalytics(true, args.getInt(POSITION), args.getString(CARD_TYPE),
                args.getString(SOURCE), args.getString(APP), args.getString(URL));
            if (commentDialogCallbackContract != null) {
              commentDialogCallbackContract.okSelected(resp, idAsLong, previousCommentId,
                  idAsString);
            }
          } else {
            ShowMessage.asSnack(this, R.string.error_occured);
            logAnalytics(false, args.getInt(POSITION), args.getString(CARD_TYPE),
                args.getString(SOURCE), args.getString(APP), args.getString(URL));
          }
        }, throwable -> {
          logAnalytics(false, args.getInt(POSITION), args.getString(CARD_TYPE),
              args.getString(SOURCE), args.getString(APP), args.getString(URL));
          CrashReport.getInstance()
              .log(throwable);
        });
  }

  private void logAnalytics(boolean success, int position, String type, String source, String app,
      String url) {
    if (commentType.equals(CommentType.TIMELINE)) {
      timelineAnalytics.sendCommentCompleted(success, position, type, source, app, url);
    }
  }

  private void disableError() {
    textInputLayout.setErrorEnabled(false);
  }

  private String getText() {
    if (textInputLayout != null) {
      return textInputLayout.getEditText()
          .getEditableText()
          .toString();
    }
    return null;
  }

  private void enableError(String error) {
    textInputLayout.setError(error);
  }

  private Observable<? extends BaseV7Response> submitComment(String inputText, long idAsLong,
      Long previousCommentId, String idAsString) {
    switch (commentType) {
      case REVIEW:
        // new comment on a review
        return PostCommentForReview.of(idAsLong, inputText, baseBodyBodyInterceptor, httpClient,
            converterFactory, tokenInvalidator, sharedPreferences)
            .observe();

      case STORE:
        // check if this is a new comment on a store or a reply to a previous one
        if (previousCommentId == null) {
          storeAnalytics.sendStoreInteractEvent("Write a Comment", "Home", appOrStoreName);
          return PostCommentForStore.of(idAsLong, inputText, baseBodyBodyInterceptor, httpClient,
              converterFactory, tokenInvalidator, sharedPreferences)
              .observe();
        }
        storeAnalytics.sendStoreInteractEvent("Reply to Comment", "Home", appOrStoreName);
        return PostCommentForStore.of(idAsLong, previousCommentId, inputText,
            baseBodyBodyInterceptor, httpClient, converterFactory, tokenInvalidator,
            sharedPreferences)
            .observe();

      case TIMELINE:
        // check if this is a new comment on a article or a reply to a previous one
        if (previousCommentId == null) {
          return PostCommentForTimelineArticle.of(idAsString, inputText, baseBodyBodyInterceptor,
              httpClient, converterFactory, tokenInvalidator, sharedPreferences)
              .observe();
        }
        return PostCommentForTimelineArticle.of(idAsString, previousCommentId, inputText,
            baseBodyBodyInterceptor, httpClient, converterFactory, tokenInvalidator,
            sharedPreferences)
            .observe();
    }
    // default case
    Logger.e(this.getTag(), "Unable to create reply due to missing comment type");
    return Observable.empty();
  }

  public void setCommentDialogCallbackContract(
      CommentDialogCallbackContract commentDialogCallbackContract) {
    this.commentDialogCallbackContract = commentDialogCallbackContract;
  }

  public void setCommentBeforeSubmissionCallbackContract(CommentBeforeSubmissionCallback callback) {
    this.commentBeforeSubmissionCallback = callback;
  }
}
