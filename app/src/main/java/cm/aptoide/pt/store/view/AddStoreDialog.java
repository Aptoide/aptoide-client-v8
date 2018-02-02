package cm.aptoide.pt.store.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
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
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.dialog.BaseDialog;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class AddStoreDialog extends BaseDialog {

  public static final int PRIVATE_STORE_INVALID_CREDENTIALS_CODE = 21;
  public static final int PRIVATE_STORE_ERROR_CODE = 22;

  private static final int COMPLETION_THRESHOLD = 1;

  private static final String TAG = AddStoreDialog.class.getName();

  private final int PRIVATE_STORE_REQUEST_CODE = 20;

  private AptoideAccountManager accountManager;
  private FragmentNavigator navigator;
  private String storeName;
  private Dialog loadingDialog;
  private SearchView searchView;
  private Button addStoreButton;
  private LinearLayout topStoresButton;
  private BodyInterceptor<BaseBody> baseBodyBodyInterceptor;
  private StoreCredentialsProvider storeCredentialsProvider;
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

      Logger.e(TAG, exception);
      throw exception;
    }
    orientationManager = new ScreenOrientationManager(activity, activity.getWindowManager());
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    subscriptions = new CompositeSubscription();

    tokenInvalidator =
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    converterFactory = WebService.getDefaultConverter();
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    httpClient = ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    storeCredentialsProvider = new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
        ((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class));
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

  @Override public void onDestroyView() {
    if (subscriptions != null && !subscriptions.isUnsubscribed()) {
      subscriptions.unsubscribe();
    }
    super.onDestroyView();
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
    }
  }

  private void bindViews(View view) {
    addStoreButton = (Button) view.findViewById(R.id.button_dialog_add_store);
    topStoresButton = (LinearLayout) view.findViewById(R.id.button_top_stores);
    searchView = (SearchView) view.findViewById(R.id.store_search_view);
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
        (AutoCompleteTextView) searchView.findViewById(
            android.support.v7.appcompat.R.id.search_src_text);
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
                Logger.i(TAG, "Timeout reached while waiting for store suggestions");
                return Single.just(suggestionCursorAdapter.getSuggestions());
              }
              Logger.w(TAG, "handleStoreRemoteQuery: ", err);
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
      loadingDialog = GenericDialogs.createGenericPleaseWaitDialog(getActivity());
    }
    orientationManager.lock();
    loadingDialog.show();
  }

  private void topStoresAction() {
    navigator.navigateTo(FragmentTopStores.newInstance(), true);
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
    new StoreUtilsProxy(accountManager, baseBodyBodyInterceptor, storeCredentialsProvider,
        AccessorFactory.getAccessorFor(((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class), httpClient,
        WebService.getDefaultConverter(), tokenInvalidator,
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences()).subscribeStore(
        getHomeMetaRequest, getStoreMeta1 -> {
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

              default:
                Snackbar.make(searchView, error.getDescription(), Snackbar.LENGTH_SHORT)
                    .show();
                break;
            }
          } else {
            Snackbar.make(searchView, R.string.error_occured, Snackbar.LENGTH_SHORT)
                .show();
          }
        }, storeName, accountManager);
  }

  void dismissLoadingDialog() {
    orientationManager.unlock();
    loadingDialog.dismiss();
  }

  private enum BundleArgs {
    STORE_NAME
  }
}
