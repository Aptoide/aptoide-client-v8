package cm.aptoide.pt.v8engine.view.account.store;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.ws.v3.CheckUserCredentialsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.SetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v3.CheckUserCredentialsJson;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.account.ErrorsMapper;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import cm.aptoide.pt.v8engine.networking.StoreBodyInterceptor;
import cm.aptoide.pt.v8engine.presenter.CreateStoreView;
import cm.aptoide.pt.v8engine.view.account.PictureLoaderFragment;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.rxbinding.view.RxView;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import okhttp3.OkHttpClient;
import org.parceler.Parcels;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.facebook.FacebookSdk.getApplicationContext;

// TODO
// create presenter and separate logic code from view
public class CreateStoreFragment extends PictureLoaderFragment implements CreateStoreView {

  public static final String ERROR_CODE_2 = "WOP-2";
  public static final String ERROR_CODE_3 = "WOP-3";
  public static final String ERROR_API_1 = "API-1";
  public static final String STORE_FROM_DEFAULT_VALUE = "store";
  private static final String STORE_FROM = "from";
  private static final String STORE_ID = "storeId";
  private static final String STORE_AVATAR = "storeAvatar";
  private static final String STORE_THEME = "storeTheme";
  private static final String STORE_DESCRIPTION = "storeDescription";
  private static final String STORE_MODEL = "store_model";
  private ProgressDialog waitDialog;

  private View storeAvatarLayout;

  private ImageView storeAvatar;
  private TextView storeHeader;
  private TextView chooseNameTitle;
  private EditText storeName;
  private EditText storeDescription;
  private Button createStoreBtn;
  private Button skipBtn;

  private StoreThemeSelector storeThemeSelector;

  private AptoideAccountManager accountManager;
  private BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptorV3;
  private BodyInterceptor<BaseBody> bodyInterceptorV7;
  private RequestBodyFactory requestBodyFactory;
  private ObjectMapper serializer;
  private IdsRepository idsRepository;
  private CreateStoreModel storeModel;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;

  public CreateStoreFragment() {
    super(false, true);
  }

  public static CreateStoreFragment newInstance() {
    return newInstance(-1, "", "", "", "");
  }

  public static CreateStoreFragment newInstance(long storeId, String storeTheme,
      String storeDescription, String storeAvatar, String storeFrom) {
    CreateStoreFragment fragment = new CreateStoreFragment();
    Bundle args = new Bundle();
    args.putLong(STORE_ID, storeId);
    args.putString(STORE_THEME, storeTheme);
    args.putString(STORE_DESCRIPTION, storeDescription);
    args.putString(STORE_AVATAR, storeAvatar);
    args.putString(STORE_FROM, storeFrom);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final V8Engine engine = (V8Engine) getActivity().getApplicationContext();
    accountManager = engine.getAccountManager();
    bodyInterceptorV3 = engine.getBaseBodyInterceptorV3();
    bodyInterceptorV7 = engine.getBaseBodyInterceptorV7();
    idsRepository = engine.getIdsRepository();
    requestBodyFactory = new RequestBodyFactory();
    httpClient = engine.getDefaultClient();
    converterFactory = WebService.getDefaultConverter();

    serializer = new ObjectMapper();
    serializer.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    waitDialog = GenericDialogs.createGenericPleaseWaitDialog(getActivity(),
        getApplicationContext().getString(R.string.please_wait_upload));
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (savedInstanceState != null && savedInstanceState.containsKey(STORE_MODEL)) {
      CreateStoreModel createStoreModel =
          Parcels.unwrap(savedInstanceState.getParcelable(STORE_MODEL));
      if (createStoreModel != null) {
        storeModel = createStoreModel;
      }
    }

    if (storeModel != null && !TextUtils.isEmpty(storeModel.getStoreAvatarPath())) {
      loadImage(Uri.parse(storeModel.getStoreAvatarPath()));
    }

    setupViewsDefaultValues(view);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    dismissWaitDialog();
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    String storeFrom = args.containsKey(STORE_FROM) ? args.getString(STORE_FROM) : "";
    long storeId = args.getLong(STORE_ID, -1);
    String storeRemoteUrl = args.containsKey(STORE_AVATAR) ? args.getString(STORE_AVATAR) : "";
    String storeTheme = args.containsKey(STORE_THEME) ? args.getString(STORE_THEME) : "";
    String storeDescription =
        args.containsKey(STORE_DESCRIPTION) ? args.getString(STORE_DESCRIPTION) : "";

    storeModel =
        new CreateStoreModel(storeId, storeFrom, storeRemoteUrl, storeTheme, storeDescription);
    storeThemeSelector = new StoreThemeSelector(storeModel);

    if (TextUtils.isEmpty(storeTheme)) {
      storeModel.setStoreThemeName(StoreThemeSelector.Theme.Default.getThemeName());
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(STORE_MODEL, Parcels.wrap(storeModel));
  }

  @Override public void loadImage(Uri imagePath) {
    if (imagePath != null && !TextUtils.isEmpty(imagePath.toString())) {
      ImageLoader.with(getActivity())
          .loadWithCircleTransform(imagePath, storeAvatar, false);
    }
  }

  @Override public void showIconPropertiesError(String errors) {
    GenericDialogs.createGenericOkMessage(getActivity(),
        getString(R.string.image_requirements_error_popup_title), errors)
        .compose(bindUntilEvent(LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  private void loadImage(String imagePath) {
    if (imagePath != null && !TextUtils.isEmpty(imagePath)) {
      ImageLoader.with(getActivity())
          .loadWithCircleTransform(imagePath, storeAvatar, false);
    }
  }

  @Override public void setupViews() {
    super.setupViews();
    setupToolbar();
    setupListeners();
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    super.setupToolbarDetails(toolbar);
    toolbar.setTitle(getActivityTitle());
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);

    createStoreBtn = (Button) view.findViewById(R.id.create_store_action);
    skipBtn = (Button) view.findViewById(R.id.create_store_skip);
    storeAvatarLayout = view.findViewById(R.id.create_store_image_action);
    storeName = (EditText) view.findViewById(R.id.create_store_name);
    storeDescription = (EditText) view.findViewById(R.id.edit_store_description);
    storeHeader = (TextView) view.findViewById(R.id.create_store_header);
    chooseNameTitle = (TextView) view.findViewById(R.id.create_store_choose_name_title);
    storeAvatar = (ImageView) view.findViewById(R.id.create_store_image);

    storeThemeSelector.bindThemeListeners(view);
  }

  private String getActivityTitle() {
    if (isCreateStore()) {
      return getString(R.string.create_store_title);
    } else {
      return getString(R.string.edit_store_title);
    }
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_create_store;
  }

  /**
   * Changes views according to context (edit or create) and shows current values for the store
   * that's being edited
   */
  private void setupViewsDefaultValues(View view) {
    if (isCreateStore()) {
      String appName = getString(R.string.app_name);
      storeHeader.setText(
          AptoideUtils.StringU.getFormattedString(R.string.create_store_header, appName));
      chooseNameTitle.setText(
          AptoideUtils.StringU.getFormattedString(R.string.create_store_name, appName));
    } else {
      storeHeader.setText(R.string.edit_store_header);
      chooseNameTitle.setText(
          AptoideUtils.StringU.getFormattedString(R.string.create_store_description_title));
      storeName.setVisibility(View.GONE);
      storeDescription.setVisibility(View.VISIBLE);
      storeDescription.setText(storeModel.getStoreDescription());
      if (storeModel.getStoreRemoteUrl() != null) {
        loadImage(storeModel.getStoreRemoteUrl());
      }
      createStoreBtn.setText(R.string.save_edit_store);
      skipBtn.setText(R.string.cancel);
    }

    // set tick to the default or user picked theme
    String storeThemeName = storeModel.getStoreThemeName();
    StoreThemeSelector.Theme theme = StoreThemeSelector.getThemeFromName(storeThemeName);
    final ImageView tick = theme.getTick(view);
    storeThemeSelector.addTickTo(tick);
  }

  private boolean isCreateStore() {
    return !STORE_FROM_DEFAULT_VALUE.equalsIgnoreCase(storeModel.getStoreFrom());
  }

  private Completable sendSkipAnalytics() {
    return Completable.fromAction(
        () -> Analytics.Account.createStore(!TextUtils.isEmpty(storeModel.getStoreAvatarPath()),
            Analytics.Account.CreateStoreAction.SKIP));
  }

  @Override public Observable<Void> selectStoreImageClick() {
    return RxView.clicks(storeAvatarLayout);
  }

  @Override public Observable<Void> createStoreClick() {
    return RxView.clicks(createStoreBtn);
  }

  @Override public Observable<Void> skipToHomeClick() {
    return RxView.clicks(skipBtn);
  }

  private void setupListeners() {

    selectStoreImageClick().compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .retry()
        .subscribe(__ -> chooseAvatarSource(), err -> CrashReport.getInstance()
            .log(err));

    skipToHomeClick().flatMap(__ -> sendSkipAnalytics().doOnCompleted(() -> navigateToHome())
        .toObservable())
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));

    createStoreClick().flatMap(aVoid -> Observable.fromCallable(() -> {
      AptoideUtils.SystemU.hideKeyboard(getActivity());
      return null;
    })
        .doOnNext(__ -> showWaitDialog())
        .flatMap(__ -> {
          final String storeName = this.storeName.getText()
              .toString()
              .trim()
              .toLowerCase();
          final String storeDescription = this.storeDescription.getText()
              .toString()
              .trim();
          return Observable.just(new CreateStoreModel(storeModel, storeName, storeDescription));
        })
        .flatMap(storeModel -> {
          final CreateStoreType createStoreType = validateData(storeModel);
          switch (createStoreType) {
            default:
            case None:
              return dismissWaitAndShowErrorMessage(R.string.nothing_inserted_store);

            case All:
            case UserAndTheme:
            case Theme:
              return createStoreType1(storeModel, createStoreType);

            case Edit:
              return createStoreType2(storeModel);

            case Edit2:
              return createStoreType3(storeModel);
          }
        }))
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .retry()
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  @NonNull private Observable<Integer> dismissWaitAndShowErrorMessage(@StringRes int stringRes) {
    return Observable.fromCallable(() -> {
      dismissWaitDialog();
      return null;
    })
        .flatMap(__ -> ShowMessage.asObservableSnack(createStoreBtn, stringRes));
  }

  private void dismissWaitDialog() {
    if (waitDialog != null && waitDialog.isShowing()) {
      waitDialog.dismiss();
    }
  }

  private void showWaitDialog() {
    if (waitDialog != null && !waitDialog.isShowing()) {
      waitDialog.show();
    }
  }

  @NonNull private Observable<Integer> dismissWaitAndShowErrorMessage(Throwable err) {
    return Observable.fromCallable(() -> {
      dismissWaitDialog();
      return null;
    })
        .flatMap(__ -> ShowMessage.asObservableSnack(createStoreBtn, err.getMessage()));
  }

  /**
   * This will not use a multipart request.
   */
  private Observable<Void> createStoreType3(CreateStoreModel storeModel) {

    return Observable.fromCallable(() -> {
      storeModel.prepareToSendRequest();
      showWaitDialog();
      return null;
    })
        .flatMap(
            __ -> SimpleSetStoreRequest.of(storeModel.getStoreId(), storeModel.getStoreThemeName(),
                storeModel.getStoreDescription(), bodyInterceptorV7, httpClient, converterFactory)
                .observe())
        .flatMap(__ -> dismissDialogAsync().andThen(accountManager.syncCurrentAccount())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> navigateToHome())
            .onErrorResumeNext(err -> {
              CrashReport.getInstance()
                  .log(err);
              return Completable.fromAction(() -> navigateToHome());
            })
            .toObservable())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(err -> {
          CrashReport.getInstance()
              .log(err);
          final int errorMessage = ErrorsMapper.getWebServiceErrorMessageFromCode(err.getMessage());
          return dismissWaitAndShowErrorMessage(errorMessage).map(__ -> null);
        })
        .map(__ -> null);
  }

  private Observable<Void> createStoreType2(CreateStoreModel storeModel) {

    return Observable.fromCallable(() -> {
      storeModel.prepareToSendRequest();
      showWaitDialog();
      return null;
    })
        .flatMap(
            __ -> SetStoreRequest.of(accountManager.getAccessToken(), storeModel.getStoreName(),
                storeModel.getStoreThemeName(), storeModel.getStoreAvatarPath(),
                storeModel.getStoreDescription(), true, storeModel.getStoreId(),
                createStoreInterceptor(storeModel), httpClient, converterFactory)
                .observe())
        .flatMap(__ -> dismissDialogAsync().andThen(accountManager.syncCurrentAccount())
            .toObservable())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(() -> navigateToHome())
        .onErrorResumeNext(err -> {
          @StringRes int errorMessage;
          if (((AptoideWsV7Exception) err).getBaseResponse()
              .getErrors()
              .get(0)
              .getCode()
              .equals(ERROR_API_1)) {
            errorMessage = R.string.ws_error_API_1;
          } else {
            errorMessage = ErrorsMapper.getWebServiceErrorMessageFromCode(err.getMessage());
          }

          return dismissWaitAndShowErrorMessage(errorMessage).filter(
              vis -> vis == ShowMessage.DISMISSED)
              .doOnCompleted(() -> navigateToHome())
              .map(__ -> null);
        })
        .map(__ -> null);
  }

  private Observable<Void> createStoreType1(@NonNull final CreateStoreModel storeModel,
      @NonNull final CreateStoreType createStoreType) {
    showWaitDialog();

    return CheckUserCredentialsRequest.of(storeModel.getStoreName(),
        accountManager.getAccessToken(), bodyInterceptorV3, httpClient, converterFactory)
        .observe()
        .observeOn(AndroidSchedulers.mainThread())
        .map(answer -> {
          dismissWaitDialog();
          if (answer.hasErrors()) {
            handleStoreCreationErrors(answer);
          } else if (!(createStoreType == CreateStoreType.Theme)) {
            onCreateSuccess(storeModel, createStoreType);
          } else {
            ShowMessage.asLongObservableSnack(getActivity(), R.string.create_store_store_created)
                .flatMap(__ -> dismissDialogAsync().andThen(accountManager.syncCurrentAccount())
                    .andThen(sendCreateAnalytics())
                    .toObservable())
                .subscribe(__ -> {
                }, err -> CrashReport.getInstance()
                    .log(err));
          }

          return null;
        })
        .onErrorResumeNext(err -> dismissWaitAndShowErrorMessage(err))
        .map(__ -> null);
  }

  private void handleStoreCreationErrors(CheckUserCredentialsJson answer) {
    if (answer.getErrors() != null
        && answer.getErrors()
        .size() > 0) {
      if (answer.getErrors()
          .get(0).code.equals(ERROR_CODE_2)) {

        GenericDialogs.createGenericContinueMessage(getActivity(), "",
            getApplicationContext().getResources()
                .getString(R.string.ws_error_WOP_2))
            .subscribe(__ -> {
            }, err -> CrashReport.getInstance()
                .log(err));
      } else if (answer.getErrors()
          .get(0).code.equals(ERROR_CODE_3)) {
        ShowMessage.asSnack(this, ErrorsMapper.getWebServiceErrorMessageFromCode(answer.getErrors()
            .get(0).code));
      } else {
        ShowMessage.asObservableSnack(this, ErrorsMapper.getWebServiceErrorMessageFromCode(
            answer.getErrors()
                .get(0).code))
            .subscribe(visibility -> {
              if (visibility == ShowMessage.DISMISSED) {
                navigateToHome();
              }
            });
      }
    }
  }

  private Completable sendCreateAnalytics() {
    return Completable.fromAction(
        () -> Analytics.Account.createStore(!TextUtils.isEmpty(storeModel.getStoreAvatarPath()),
            Analytics.Account.CreateStoreAction.CREATE));
  }

  /**
   * This method validates the user data inserted when the create store button is pressed and
   * returns a code for the corresponding create store remote request.
   */

  private CreateStoreType validateData(@NonNull final CreateStoreModel storeModel) {
    if (STORE_FROM_DEFAULT_VALUE.equals(storeModel.getStoreFrom())) {
      if (storeModel.hasStoreDescription() || storeModel.hasThemeName()) {
        if (storeModel.hasStoreAvatar()) {
          return CreateStoreType.Edit;
        } else {
          return CreateStoreType.Edit2;
        }
      }
    } else {
      if (storeModel.hasStoreName()) {
        if (storeModel.hasStoreAvatar()) {
          return CreateStoreType.All;
        } else if (storeModel.hasThemeName()) {
          return CreateStoreType.UserAndTheme;
        } else {
          return CreateStoreType.Theme;
        }
      }
    }
    return CreateStoreType.None;
  }

  private void navigateToHome() {
    dismissWaitDialog();

    if(isCreateStore()){
      getFragmentNavigator().navigateToHomeCleaningBackStack();
      return;
    }

    getFragmentNavigator().back();
  }

  private void onCreateSuccess(@NonNull final CreateStoreModel storeModel,
      @NonNull final CreateStoreType createStoreType) {
    ShowMessage.asSnack(this, R.string.create_store_store_created);
    if (createStoreType == CreateStoreType.All) {
      /*
       * Multipart
       */
      storeModel.prepareToSendRequest();
      SetStoreRequest.of(accountManager.getAccessToken(), storeModel.getStoreName(),
          storeModel.getStoreThemeName(), storeModel.getStoreAvatarPath(),
          createStoreInterceptor(storeModel), httpClient, converterFactory)
          .observe()
          .timeout(90, TimeUnit.SECONDS)
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap(__ -> dismissDialogAsync().andThen(accountManager.syncCurrentAccount())
              .andThen(sendCreateAnalytics())
              .toObservable())
          .doOnNext(__ -> navigateToHome())
          .subscribe(__ -> {
          }, err -> {
            dismissWaitDialog();
            if (err instanceof SocketTimeoutException) {
              ShowMessage.asLongObservableSnack(getActivity(), R.string.store_upload_photo_failed)
                  .subscribe(visibility -> {
                    if (visibility == ShowMessage.DISMISSED) {
                      navigateToHome();
                    }
                  });
            } else if (err instanceof TimeoutException) {
              ShowMessage.asLongObservableSnack(getActivity(), R.string.store_upload_photo_failed)
                  .subscribe(visibility -> {
                    if (visibility == ShowMessage.DISMISSED) {
                      navigateToHome();
                    }
                  });
            } else {
              try {
                if (((AptoideWsV7Exception) err).getBaseResponse()
                    .getErrors()
                    .get(0)
                    .getCode()
                    .equals(ERROR_API_1)) {
                  ShowMessage.asLongObservableSnack(getActivity(), R.string.ws_error_API_1)
                      .subscribe(visibility -> {
                        if (visibility == ShowMessage.DISMISSED) {
                          navigateToHome();
                        }
                      });
                  return;
                }
              } catch (ClassCastException e) {

              }

              ShowMessage.asLongObservableSnack(getActivity(),
                  ErrorsMapper.getWebServiceErrorMessageFromCode(err.getMessage()))
                  .subscribe(visibility -> {
                    if (visibility == ShowMessage.DISMISSED) {
                      navigateToHome();
                    }
                  });
            }
          });
    } else if (createStoreType == CreateStoreType.UserAndTheme
        || createStoreType == CreateStoreType.Theme) {
      /*
       * not multipart
       */
      storeModel.prepareToSendRequest();

      SimpleSetStoreRequest.of(storeModel.getStoreName(), storeModel.getStoreThemeName(),
          bodyInterceptorV7, httpClient, converterFactory)
          .observe()
          .flatMap(__ -> dismissDialogAsync().andThen(accountManager.syncCurrentAccount())
              .andThen(sendCreateAnalytics())
              .toObservable())
          .doOnNext(__ -> navigateToHome())
          .subscribe(__ -> {
          }, err -> {
            waitDialog.dismiss();
            @StringRes int reason =
                ErrorsMapper.getWebServiceErrorMessageFromCode(err.getMessage());
            ShowMessage.asSnack(createStoreBtn, reason);
            CrashReport.getInstance()
                .log(err);
          });
    }
  }

  @NonNull private Completable dismissDialogAsync() {
    return Completable.fromAction(() -> dismissWaitDialog());
  }

  @NonNull private StoreBodyInterceptor createStoreInterceptor(CreateStoreModel storeModel) {
    return new StoreBodyInterceptor(idsRepository.getUniqueIdentifier(), accountManager,
        requestBodyFactory, storeModel.getStoreThemeName(), storeModel.getStoreDescription(),
        serializer);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Uri avatarUrl = null;
    final Context applicationContext = getActivity().getApplicationContext();

    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
      avatarUrl = getFileUriFromFileName(photoFileName);
    }

    if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK) {
      avatarUrl = data.getData();
    }

    try {
      final String filePath = new FileUtils().getMediaStoragePath(avatarUrl, applicationContext);
      storeModel.setStoreAvatarPath(filePath);
      checkAvatarRequirements(filePath, avatarUrl);
    } catch (NullPointerException ex) {
      CrashReport.getInstance()
          .log(ex);
    }
  }

  /**
   * 1: all (Multipart)  2: user and theme 3:user 4/5:edit
   */
  private enum CreateStoreType {
    None(0), All(1), UserAndTheme(2), Theme(3), Edit(4), Edit2(5);

    private final int value;

    CreateStoreType(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

  private static class StoreThemeSelector {

    private final CreateStoreModel storeModel;

    public StoreThemeSelector(CreateStoreModel storeModel) {
      this.storeModel = storeModel;
    }

    public static Theme getThemeFromName(String themeName) {
      if (!TextUtils.isEmpty(themeName)) {
        for (final Theme t : Theme.values()) {
          if (themeName.equalsIgnoreCase(t.getThemeName())) {
            return t;
          }
        }
      }
      return Theme.Default;
    }

    private void bindThemeListeners(final View rootView) {
      for (final Theme t : Theme.values()) {
        RxView.clicks(t.getShape(rootView))
            .doOnNext(__ -> {
              String themeName = storeModel.getStoreThemeName();
              Theme theme = getThemeFromName(themeName);
              removeTickFrom(theme.getTick(rootView));
            })
            .doOnNext(__ -> addTickTo(t.getTick(rootView)))
            .doOnNext(__ -> storeModel.setStoreThemeName(t.getThemeName()))
            .subscribe(__ -> {
            }, err -> CrashReport.getInstance()
                .log(err));
      }
    }

    private void removeTickFrom(ImageView tickImage) {
      if (tickImage != null) {
        tickImage.setVisibility(View.GONE);
      }
    }

    private void addTickTo(ImageView tickImage) {
      if (tickImage != null) {
        tickImage.setVisibility(View.VISIBLE);
      }
    }

    private enum Theme {
      Default("default", R.id.create_store_theme_default,
          R.id.create_store_theme_check_default), Orange("orange", R.id.create_store_theme_orange,
          R.id.create_store_theme_check_orange), Green("green", R.id.create_store_theme_green,
          R.id.create_store_theme_check_green), Red("red", R.id.create_store_theme_red,
          R.id.create_store_theme_check_red), Indigo("indigo", R.id.create_store_theme_indigo,
          R.id.create_store_theme_check_indigo), Teal("teal", R.id.create_store_theme_teal,
          R.id.create_store_theme_check_teal), Pink("orange", R.id.create_store_theme_pink,
          R.id.create_store_theme_check_pink), Lime("lime", R.id.create_store_theme_lime,
          R.id.create_store_theme_check_lime), Amber("amber", R.id.create_store_theme_amber,
          R.id.create_store_theme_check_amber), Brown("brown", R.id.create_store_theme_brown,
          R.id.create_store_theme_check_brown), LightBlue("light-blue",
          R.id.create_store_theme_lightblue, R.id.create_store_theme_check_lightblue), Black(
          "black", R.id.create_store_theme_black, R.id.create_store_theme_check_black), BlueGrey(
          "blue-grey", R.id.create_store_theme_blue_grey,
          R.id.create_store_theme_check_blue_grey), DeepPurple("deep-purple",
          R.id.create_store_theme_deep_purple,
          R.id.create_store_theme_check_deep_purple), LightGreen("light-green",
          R.id.create_store_theme_light_green, R.id.create_store_theme_check_light_green), Grey(
          "grey", R.id.create_store_theme_grey, R.id.create_store_theme_check_grey);

      private final String color;
      private final int imageViewShapeId;
      private final int imageViewTickId;

      Theme(String color, int imageViewShapeId, int imageViewTickId) {
        this.color = color;
        this.imageViewShapeId = imageViewShapeId;
        this.imageViewTickId = imageViewTickId;
      }

      public String getThemeName() {
        return color;
      }

      public ImageView getShape(View rootView) {
        return (ImageView) rootView.findViewById(imageViewShapeId);
      }

      public ImageView getTick(View rootView) {
        return (ImageView) rootView.findViewById(imageViewTickId);
      }
    }
  }
}
