package cm.aptoide.pt.v8engine.view.store;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.database.AccessorFactory;
import cm.aptoide.pt.v8engine.search.websocket.StoreAutoCompleteWebSocket;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreUtils;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.MainActivity;
import cm.aptoide.pt.v8engine.view.dialog.BaseDialog;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.view.search.StoreSearchActivity;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created with IntelliJ IDEA. User: rmateus Date: 18-10-2013 Time: 17:27 To change this template
 * use File | Settings |
 * File Templates.
 */
public class AddStoreDialog extends BaseDialog {

  public static final int PRIVATE_STORE_INVALID_CREDENTIALS_CODE = 21;
  public static final int PRIVATE_STORE_ERROR_CODE = 22;
  private static final String TAG = AddStoreDialog.class.getName();
  private static StoreAutoCompleteWebSocket storeAutoCompleteWebSocket;

  private final int PRIVATE_STORE_REQUEST_CODE = 20;

  private AptoideAccountManager accountManager;
  private FragmentNavigator navigator;
  private String storeName;
  private Dialog loadingDialog;
  private SearchView searchView;
  private Button addStoreButton;
  private LinearLayout topStoresButton;
  private TextView topStoreText1;
  private TextView topStoreText2;
  private ImageView image;
  private String givenStoreName;
  private BodyInterceptor<BaseBody> baseBodyBodyInterceptor;
  private StoreCredentialsProvider storeCredentialsProvider;
  private SearchView.SearchAutoComplete searchAutoComplete;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof MainActivity) {
      navigator = ((MainActivity) activity).getFragmentNavigator();
    } else {
      Logger.e(TAG, "Launched AddStoreDialog from invalid Activity");
      throw new IllegalStateException();
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    tokenInvalidator = ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator();
    converterFactory = WebService.getDefaultConverter();
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    httpClient = ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    storeCredentialsProvider = new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
        ((V8Engine) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class));
    baseBodyBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();

    if (savedInstanceState != null) {
      storeName = savedInstanceState.getString(BundleArgs.STORE_NAME.name());
    }
  }

  @Override public void onViewCreated(final View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    bindViews(view);
    setupSearchView();
    setupStoreSearch(searchView);
    RxView.clicks(addStoreButton)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(click -> {
          addStoreAction();
        });
    RxView.clicks(topStoresButton)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(click -> topStoresAction());

    RxView.clicks(topStoreText1)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(click -> topStoresAction());

    RxView.clicks(topStoreText2)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(click -> topStoresAction());
  }

  @Override public void onDetach() {
    super.onDetach();
    if (storeAutoCompleteWebSocket != null) {
      storeAutoCompleteWebSocket.disconnect();
    }
  }

  @Override public void show(FragmentManager manager, String tag) {
    if (navigator == null) {
      Logger.w(TAG, FragmentNavigator.class.getName() + " is null.");
    }
    super.show(manager, tag);
  }

  @Override public int show(FragmentTransaction transaction, String tag) {
    if (navigator == null) {
      Logger.w(TAG, FragmentNavigator.class.getName() + " is null.");
    }
    return super.show(transaction, tag);
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
          ShowMessage.asSnack(this, R.string.ws_error_invalid_grant);
          break;
        case PRIVATE_STORE_ERROR_CODE:
        default:
          ShowMessage.asSnack(this, R.string.error_occured);
      }
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    if (getDialog() != null) {
      getDialog().getWindow()
          .requestFeature(Window.FEATURE_NO_TITLE);
    }
    return inflater.inflate(R.layout.dialog_add_store, container, false);
  }

  private void addStoreAction() {
    givenStoreName = searchView.getQuery()
        .toString();
    if (givenStoreName.length() > 0) {
      AddStoreDialog.this.storeName = givenStoreName;
      getStore(givenStoreName);
      showLoadingDialog();
    }
  }

  private void bindViews(View view) {
    searchView = (SearchView) view.findViewById(R.id.edit_store_uri);
    addStoreButton = (Button) view.findViewById(R.id.button_dialog_add_store);
    topStoresButton = (LinearLayout) view.findViewById(R.id.button_top_stores);
    topStoreText1 = (TextView) view.findViewById(R.id.top_stores_text_1);
    topStoreText2 = (TextView) view.findViewById(R.id.top_stores_text_2);
    image = (ImageView) view.findViewById(R.id.search_mag_icon);
    searchAutoComplete = (SearchView.SearchAutoComplete) view.findViewById(R.id.search_src_text);
  }

  private void setupSearchView() {
    searchView.setIconifiedByDefault(false);
    image.setImageDrawable(null);
    searchAutoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override public void onFocusChange(View view, boolean b) {
        if (getDialog() != null) {
          if (!searchAutoComplete.isFocused() && getDialog().isShowing() && isResumed()) {
            dismiss();
          }
        }
      }
    });
  }

  private void setupStoreSearch(SearchView searchView) {
    final SearchManager searchManager = (SearchManager) getContext().getApplicationContext()
        .getSystemService(Context.SEARCH_SERVICE);
    ComponentName cn =
        new ComponentName(getContext().getApplicationContext(), StoreSearchActivity.class);
    searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
    storeAutoCompleteWebSocket = new StoreAutoCompleteWebSocket();

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {
        givenStoreName = query;
        searchView.setQuery(givenStoreName, false);
        addStoreAction();
        return true;
      }

      @Override public boolean onQueryTextChange(String newText) {
        //searchView.setQuery(newText, false);
        return false;
      }
    });

    searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
      @Override public boolean onSuggestionSelect(int position) {
        return false;
      }

      @Override public boolean onSuggestionClick(int position) {
        Cursor item = (Cursor) searchView.getSuggestionsAdapter()
            .getItem(position);
        givenStoreName = item.getString(1);
        searchView.setQuery(givenStoreName, false);
        return true;
      }
    });

    searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
      @Override public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus) {
          storeAutoCompleteWebSocket.disconnect();
        }
      }
    });

    searchView.setOnSearchClickListener(
        v -> storeAutoCompleteWebSocket.connect(StoreAutoCompleteWebSocket.STORE_WEBSOCKET_PORT));
  }

  private void getStore(String storeName) {
    GetStoreMetaRequest getHomeMetaRequest = buildRequest(storeName);

    executeRequest(getHomeMetaRequest);
  }

  private void showLoadingDialog() {

    if (loadingDialog == null) {
      loadingDialog = GenericDialogs.createGenericPleaseWaitDialog(getActivity());
    }

    loadingDialog.show();
  }

  private void topStoresAction() {
    navigator.navigateTo(V8Engine.getFragmentProvider()
        .newFragmentTopStores());
    if (isAdded()) {
      dismiss();
    }
  }

  private GetStoreMetaRequest buildRequest(String storeName) {
    return GetStoreMetaRequest.of(
        StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider),
        baseBodyBodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
  }

  private void executeRequest(GetStoreMetaRequest getHomeMetaRequest) {
    new StoreUtilsProxy(accountManager, baseBodyBodyInterceptor, storeCredentialsProvider,
        AccessorFactory.getAccessorFor(((V8Engine) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class), httpClient,
        WebService.getDefaultConverter(), tokenInvalidator,
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences()).subscribeStore(
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
                ShowMessage.asSnack(this, error.getDescription());
            }
          } else {
            ShowMessage.asSnack(this, R.string.error_occured);
          }
        }, storeName, accountManager);
  }

  void dismissLoadingDialog() {
    loadingDialog.dismiss();
  }

  private enum BundleArgs {
    STORE_NAME,
  }
}
