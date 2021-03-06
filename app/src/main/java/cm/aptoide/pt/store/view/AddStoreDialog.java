package cm.aptoide.pt.store.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.search.SuggestionCursorAdapter;
import cm.aptoide.pt.search.suggestions.SearchSuggestionManager;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.fragment.BaseDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class AddStoreDialog extends BaseDialogFragment {

  public static final int PRIVATE_STORE_INVALID_CREDENTIALS_CODE = 21;
  public static final int PRIVATE_STORE_ERROR_CODE = 22;

  private static final int COMPLETION_THRESHOLD = 1;

  private static final String TAG = AddStoreDialog.class.getName();

  private final int PRIVATE_STORE_REQUEST_CODE = 20;

  @Inject StoreCredentialsProvider storeCredentialsProvider;
  @Inject StoreUtilsProxy storeUtilsProxy;
  private AptoideAccountManager accountManager;
  private FragmentNavigator navigator;
  private String storeName;
  private Dialog loadingDialog;
  private SearchView searchView;
  private RelativeLayout searchViewLayout;
  private TextView errorMessage;
  private Button addStoreButton;
  private Button topStoresButton;
  private BodyInterceptor<BaseBody> baseBodyBodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;
  private StoreAnalytics storeAnalytics;
  private AnalyticsManager analyticsManager;
  private NavigationTracker navigationTracker;
  private ScreenOrientationManager orientationManager;

  private SearchSuggestionManager searchSuggestionManager;
  private CompositeSubscription subscriptions;

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (ActivityResultNavigator.class.isAssignableFrom(activity.getClass())) {
      navigator = ((ActivityResultNavigator) activity).getFragmentNavigator();
    } else {
      final IllegalStateException exception = new IllegalStateException(
          AddStoreDialog.class.getSimpleName()
              + " must extend class "
              + ActivityResultNavigator.class.getSimpleName());

      Logger.getInstance()
          .e(TAG, exception);
      throw exception;
    }
    orientationManager = new ScreenOrientationManager(activity, activity.getWindowManager());
  }

  @Override public void onResume() {
    super.onResume();
    final Dialog dialog = getDialog();
    Rect rect = new Rect();
    Window window = getActivity().getWindow();
    window.getDecorView()
        .getWindowVisibleDisplayFrame(rect);
    double width = rect.width() * 0.8;
    if (dialog != null) {
      if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
        dialog.getWindow()
            .setLayout(Math.round((float) width), WRAP_CONTENT);
      } else {
        dialog.getWindow()
            .setLayout(MATCH_PARENT, WRAP_CONTENT);
      }
    }
  }

  @Override public void onDestroyView() {
    if (subscriptions != null && !subscriptions.isUnsubscribed()) {
      subscriptions.unsubscribe();
    }
    super.onDestroyView();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);

    subscriptions = new CompositeSubscription();

    tokenInvalidator =
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    converterFactory = WebService.getDefaultConverter();
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    httpClient = ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    baseBodyBodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();

    if (savedInstanceState != null) {
      storeName = savedInstanceState.getString(BundleArgs.STORE_NAME.name());
    }
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    analyticsManager = application.getAnalyticsManager();
    navigationTracker = application.getNavigationTracker();
    storeAnalytics = new StoreAnalytics(analyticsManager, navigationTracker);

    searchSuggestionManager = application.getSearchSuggestionManager();
  }

  @Override public void onViewCreated(final View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    bindViews(view);
    setupSearch();
    setupButtonHandlers();
    dismissIfFocusIsLost();
  }

  private void dismissIfFocusIsLost() {
    subscriptions.add(RxView.focusChanges(searchView)
        .skip(300, TimeUnit.MILLISECONDS) // enough time to render the view
        .filter(event -> !event)
        .subscribe(event -> {
          final Dialog dialog = AddStoreDialog.this.getDialog();
          if (dialog != null && dialog.isShowing() && isResumed()) {
            dialog.dismiss();
          }
        }));
  }

  private void setupButtonHandlers() {
    subscriptions.add(RxView.clicks(addStoreButton)
        .subscribe(click -> {
          setDefaultState();
          addStoreAction();
          storeAnalytics.sendStoreTabInteractEvent("Add Store", true);
        }));

    subscriptions.add(RxView.clicks(topStoresButton)
        .subscribe(click -> topStoresAction()));
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(BundleArgs.STORE_NAME.name(), storeName);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == PRIVATE_STORE_REQUEST_CODE) {
      switch (resultCode) {
        case Activity.RESULT_OK:
          dismiss();
          break;
        case PRIVATE_STORE_INVALID_CREDENTIALS_CODE:
          Snackbar.make(searchView, R.string.ws_error_invalid_grant, Snackbar.LENGTH_SHORT)
              .show();
          break;
        case PRIVATE_STORE_ERROR_CODE:
        default:
          Snackbar.make(searchView, R.string.error_occured, Snackbar.LENGTH_SHORT)
              .show();
          break;
      }
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final Dialog dialog = getDialog();
    if (dialog != null) {
      dialog.getWindow()
          .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
      dialog.getWindow()
          .requestFeature(Window.FEATURE_NO_TITLE);
      dialog.setCancelable(true);
    }
    return inflater.inflate(R.layout.dialog_add_store, container, false);
  }

  private void addStoreAction(String storeName) {
    AddStoreDialog.this.storeName = storeName;
    getStore(storeName);
    showLoadingDialog();
  }

  @Deprecated private void addStoreAction() {
    String givenStoreName = searchView.getQuery()
        .toString();
    if (givenStoreName.length() > 0) {
      AddStoreDialog.this.storeName = givenStoreName;
      getStore(givenStoreName);
      showLoadingDialog();
    } else {
      searchViewLayout.setBackground(
          getResources().getDrawable(R.drawable.add_stores_dialog_seach_box_error));
      errorMessage.setVisibility(View.VISIBLE);
      errorMessage.setText(R.string.add_store_dialog_no_query);
    }
  }

  private void bindViews(View view) {
    addStoreButton = view.findViewById(R.id.button_dialog_add_store);
    topStoresButton = view.findViewById(R.id.button_top_stores);
    searchView = view.findViewById(R.id.store_search_view);
    searchViewLayout = view.findViewById(R.id.search_box_layout);
    errorMessage = view.findViewById(R.id.error_message);
    EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
    searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
    searchEditText.setHintTextColor(getResources().getColor(R.color.grey));
  }

  private void setupSearch() {
    final SuggestionCursorAdapter suggestionCursorAdapter =
        new SuggestionCursorAdapter(getContext());
    searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
      @Override public boolean onSuggestionSelect(int position) {
        return false;
      }

      @Override public boolean onSuggestionClick(int position) {
        searchView.setQuery(suggestionCursorAdapter.getSuggestionAt(position), false);
        return true;
      }
    });
    searchView.setSuggestionsAdapter(suggestionCursorAdapter);

    final AutoCompleteTextView autoCompleteTextView =
        searchView.findViewById(androidx.appcompat.R.id.search_src_text);
    autoCompleteTextView.setThreshold(COMPLETION_THRESHOLD);

    handleEmptyQuery(suggestionCursorAdapter);
    handleSubmittedQuery();
    handleStoreRemoteQuery(suggestionCursorAdapter);
  }

  private void handleStoreRemoteQuery(SuggestionCursorAdapter suggestionCursorAdapter) {
    subscriptions.add(RxSearchView.queryTextChangeEvents(searchView)
        .filter(event -> !event.isSubmitted())
        .map(event -> event.queryText()
            .toString())
        .filter(query -> query != null && query.length() >= COMPLETION_THRESHOLD)
        .flatMapSingle(query -> searchSuggestionManager.getSuggestionsForStore(query)
            .onErrorResumeNext(err -> {
              if (err instanceof TimeoutException) {
                Logger.getInstance()
                    .i(TAG, "Timeout reached while waiting for store suggestions");
                return Single.just(suggestionCursorAdapter.getSuggestions());
              }
              Logger.getInstance()
                  .w(TAG, "handleStoreRemoteQuery: ", err);
              return Single.error(err);
            })
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess(data -> suggestionCursorAdapter.setData(data)))
        .retry()
        .subscribe());
  }

  private void handleSubmittedQuery() {
    subscriptions.add(RxSearchView.queryTextChangeEvents(searchView)
        .observeOn(AndroidSchedulers.mainThread())
        .filter(event -> event.isSubmitted())
        .map(event -> event.queryText()
            .toString())
        .doOnNext(query -> addStoreAction(query))
        .subscribe());
  }

  private void handleEmptyQuery(SuggestionCursorAdapter suggestionCursorAdapter) {
    subscriptions.add(RxSearchView.queryTextChangeEvents(searchView)
        .observeOn(AndroidSchedulers.mainThread())
        .filter(event -> event.queryText()
            .length() == 0)
        .doOnNext(__ -> suggestionCursorAdapter.setData(Collections.emptyList()))
        .subscribe());
  }

  private void getStore(String storeName) {
    executeRequest(buildRequest(storeName));
  }

  private void showLoadingDialog() {

    if (loadingDialog == null) {
      loadingDialog = GenericDialogs.createGenericPleaseWaitDialog(getActivity(),
          themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId);
    }
    orientationManager.lock();
    loadingDialog.show();
  }

  private void topStoresAction() {
    navigator.navigateTo(TopStoresFragment.newInstance(), true);
    if (isAdded()) {
      dismiss();
    }
  }

  private GetStoreMetaRequest buildRequest(String storeName) {
    return GetStoreMetaRequest.of(
        StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider),
        baseBodyBodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());
  }

  private void executeRequest(GetStoreMetaRequest getHomeMetaRequest) {
    storeUtilsProxy.subscribeStore(getHomeMetaRequest, getStoreMeta1 -> {
      ShowMessage.asSnack(getView(),
          AptoideUtils.StringU.getFormattedString(R.string.store_followed,
              getContext().getResources(), storeName));

      dismissLoadingDialog();
      dismiss();
    }, e -> {
      dismissLoadingDialog();
      if (e instanceof AptoideWsV7Exception) {
        BaseV7Response baseResponse = ((AptoideWsV7Exception) e).getBaseResponse();
        BaseV7Response.Error error = baseResponse.getError();
        switch (StoreUtils.getErrorType(error.getCode())) {
          case PRIVATE_STORE_ERROR:
            DialogFragment dialogFragment = PrivateStoreDialog.newInstance(AddStoreDialog
                .this, PRIVATE_STORE_REQUEST_CODE, storeName, false);
            dialogFragment.show(getFragmentManager(), PrivateStoreDialog.class.getName());
            break;

          case STORE_DOESNT_EXIST:
            searchViewLayout.setBackground(
                getResources().getDrawable(R.drawable.add_stores_dialog_seach_box_error));
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText(error.getDescription());
            break;

          default:
            searchViewLayout.setBackground(
                getResources().getDrawable(R.drawable.add_stores_dialog_seach_box_error));
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText(error.getDescription());
            break;
        }
      } else {
        searchViewLayout.setBackground(
            getResources().getDrawable(R.drawable.add_stores_dialog_seach_box_error));
        errorMessage.setVisibility(View.VISIBLE);
        errorMessage.setText(R.string.error_occured);
      }
    }, storeName, accountManager);
  }

  void dismissLoadingDialog() {
    orientationManager.unlock();
    loadingDialog.dismiss();
  }

  private void setDefaultState() {
    errorMessage.setVisibility(View.INVISIBLE);
    searchViewLayout.setBackground(
        getResources().getDrawable(R.drawable.add_stores_dialog_search_box_border));
  }

  private enum BundleArgs {
    STORE_NAME
  }
}
