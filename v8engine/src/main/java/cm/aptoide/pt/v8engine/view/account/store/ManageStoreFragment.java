package cm.aptoide.pt.v8engine.view.account.store;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.store.StoreTheme;
import cm.aptoide.pt.v8engine.view.account.ImageLoaderFragment;
import cm.aptoide.pt.v8engine.view.custom.DividerItemDecoration;
import cm.aptoide.pt.v8engine.view.dialog.ImageSourceSelectionDialogFragment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import com.trello.rxlifecycle.android.FragmentEvent;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.parceler.Parcels;
import retrofit2.Converter;
import rx.Observable;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ManageStoreFragment extends ImageLoaderFragment
    implements ManageStoreView, ImageSourceSelectionDialogFragment.ImageSourceSelectionHandler {

  private static final String TAG = ManageStoreFragment.class.getName();

  private static final String EXTRA_STORE_MODEL = "store_model";
  private static final String EXTRA_GO_TO_HOME = "go_to_home";

  private TextView header;
  private TextView chooseStoreNameTitle;
  private View selectStoreImageButton;
  private ImageView storeImage;
  private Button saveDataButton;
  private Button cancelChangesButton;
  private EditText storeName;
  private EditText storeDescription;
  private ProgressDialog waitDialog;

  private RecyclerView themeSelectorView;
  private ThemeSelectorViewAdapter themeSelectorAdapter;

  private ManageStoreViewModel currentModel;
  private boolean goToHome;

  public static ManageStoreFragment newInstance(ManageStoreViewModel storeModel, boolean goToHome) {
    Bundle args = new Bundle();
    args.putParcelable(EXTRA_STORE_MODEL, Parcels.wrap(storeModel));
    args.putBoolean(EXTRA_GO_TO_HOME, goToHome);

    ManageStoreFragment fragment = new ManageStoreFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public Observable<Void> selectStoreImageClick() {
    return RxView.clicks(selectStoreImageButton);
  }

  @Override public Observable<ManageStoreViewModel> saveDataClick() {
    return RxView.clicks(saveDataButton)
        .map(__ -> updateAndGetStoreModel());
  }

  @Override public Observable<Void> cancelClick() {
    return RxView.clicks(cancelChangesButton);
  }

  @Override public void showLoadImageDialog() {
    DialogFragment dialogFragment = new ImageSourceSelectionDialogFragment();
    dialogFragment.setTargetFragment(this, 0);
    dialogFragment.show(getChildFragmentManager(), "imageSourceChooser");
  }

  @Override public void navigateHome() {
    getFragmentNavigator().navigateToHomeCleaningBackStack();
  }

  @Override public void navigateBack() {
    getFragmentNavigator().popBackStack();
  }

  @Override public void showError(@StringRes int errorMessage) {
    ShowMessage.asSnack(this, errorMessage);
  }

  @Override public void showGenericError() {
    ShowMessage.asSnack(this, R.string.having_some_trouble);
  }

  @Override public void showWaitProgressBar() {
    if (waitDialog != null && !waitDialog.isShowing()) {
      waitDialog.show();
    }
  }

  @Override public void dismissWaitProgressBar() {
    if (waitDialog != null && waitDialog.isShowing()) {
      waitDialog.dismiss();
    }
  }

  @Override public void onDestroyView() {
    dismissWaitProgressBar();
    super.onDestroyView();
  }

  @Override public void setupViews() {
    super.setupViews();

    storeName.setText(currentModel.getStoreName());
    storeDescription.setText(currentModel.getStoreDescription());

    themeSelectorView.setLayoutManager(
        new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    PublishRelay<StoreTheme> storeThemePublishRelay = PublishRelay.create();
    themeSelectorAdapter = new ThemeSelectorViewAdapter(storeThemePublishRelay);
    themeSelectorView.setAdapter(themeSelectorAdapter);

    themeSelectorAdapter.storeThemeSelection()
        .doOnNext(storeTheme -> {
          currentModel.setStoreThemeName(storeTheme.getThemeName());
        })
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe();

    themeSelectorView.addItemDecoration(new DividerItemDecoration(getContext(), 8,
        DividerItemDecoration.LEFT | DividerItemDecoration.RIGHT));

    themeSelectorAdapter.selectTheme(currentModel.getStoreThemeName());

    setupViewsDefaultDataUsingStore(currentModel);

    final V8Engine engine = (V8Engine) getActivity().getApplicationContext();
    attachPresenter(buildPresenter(engine, goToHome), null);
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    super.setupToolbarDetails(toolbar);
    toolbar.setTitle(getViewTitle(currentModel));
  }

  @Override public void bindViews(@Nullable View view) {
    super.bindViews(view);
    header = (TextView) view.findViewById(R.id.create_store_header);
    chooseStoreNameTitle = (TextView) view.findViewById(R.id.create_store_choose_name_title);
    selectStoreImageButton = view.findViewById(R.id.create_store_image_action);
    storeImage = (ImageView) view.findViewById(R.id.create_store_image);
    storeName = (EditText) view.findViewById(R.id.create_store_name);
    storeDescription = (EditText) view.findViewById(R.id.edit_store_description);
    cancelChangesButton = (Button) view.findViewById(R.id.create_store_skip);
    saveDataButton = (Button) view.findViewById(R.id.create_store_action);
    themeSelectorView = (RecyclerView) view.findViewById(R.id.theme_selector);

    waitDialog = GenericDialogs.createGenericPleaseWaitDialog(getActivity(),
        getApplicationContext().getString(R.string.please_wait_upload));
  }

  private ManageStoreViewModel updateAndGetStoreModel() {
    currentModel = ManageStoreViewModel.from(currentModel, storeName.getText()
        .toString(), storeDescription.getText()
        .toString());
    currentModel.setStoreThemeName(themeSelectorAdapter.getSelectedThemeName());
    return currentModel;
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_manage_store;
  }

  @Override public void loadExtras(@Nullable Bundle args) {
    super.loadExtras(args);
    if (args != null) {
      try {
        currentModel = Parcels.unwrap(args.getParcelable(EXTRA_STORE_MODEL));
      } catch (NullPointerException ex) {
        currentModel = new ManageStoreViewModel();
      }
      goToHome = args.getBoolean(EXTRA_GO_TO_HOME, true);
    }
  }

  @NonNull private ManageStorePresenter buildPresenter(@NonNull V8Engine engine, boolean goToHome) {
    AptoideAccountManager accountManager = engine.getAccountManager();
    BodyInterceptor<BaseBody> bodyInterceptorV3 = engine.getBaseBodyInterceptorV3();
    BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorV7 =
        engine.getBaseBodyInterceptorV7();
    //IdsRepository idsRepository = engine.getIdsRepository();
    RequestBodyFactory requestBodyFactory = new RequestBodyFactory();
    OkHttpClient httpClient = engine.getDefaultClient();
    Converter.Factory converterFactory = WebService.getDefaultConverter();
    ObjectMapper objectMapper = engine.getNonNullObjectMapper();

    final BodyInterceptor<HashMapNotNull<String, RequestBody>> multipartBodyInterceptor =
        engine.getMultipartBodyInterceptor();

    StoreManagerFactory storeManagerFactory =
        new StoreManagerFactory(accountManager, httpClient, converterFactory,
            multipartBodyInterceptor, bodyInterceptorV3, bodyInterceptorV7, requestBodyFactory,
            objectMapper);
    return new ManageStorePresenter(this, CrashReport.getInstance(), goToHome,
        storeManagerFactory.create());
  }

  private void setupViewsDefaultDataUsingStore(ManageStoreViewModel storeModel) {
    if (!storeModel.storeExists()) {
      String appName = getString(R.string.app_name);
      header.setText(
          AptoideUtils.StringU.getFormattedString(R.string.create_store_header, appName));
      chooseStoreNameTitle.setText(
          AptoideUtils.StringU.getFormattedString(R.string.create_store_name, appName));
    } else {
      header.setText(R.string.edit_store_header);
      chooseStoreNameTitle.setText(
          AptoideUtils.StringU.getFormattedString(R.string.create_store_description_title));
      storeName.setVisibility(View.GONE);
      storeDescription.setVisibility(View.VISIBLE);
      storeDescription.setText(storeModel.getStoreDescription());
      final String storeImagePath = storeModel.getStoreImagePath();
      if (!TextUtils.isEmpty(storeImagePath)) {
        loadImage(Uri.parse(storeImagePath));
      }
      saveDataButton.setText(R.string.save_edit_store);
      cancelChangesButton.setText(R.string.cancel);
    }
  }

  private String getViewTitle(ManageStoreViewModel storeModel) {
    if (!storeModel.storeExists()) {
      return getString(R.string.create_store_title);
    } else {
      return getString(R.string.edit_store_title);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(EXTRA_STORE_MODEL, Parcels.wrap(currentModel));
    outState.putBoolean(EXTRA_GO_TO_HOME, goToHome);
  }

  private void loadImageFromCamera() {
    requestAccessToCamera(() -> {
      dispatchTakePictureIntent();
    }, () -> {
      Logger.e(TAG, "User denied access to camera");
    });
  }

  @Override public void loadImage(Uri imagePath) {
    ImageLoader.with(getActivity())
        .loadUsingCircleTransform(imagePath, storeImage);
  }

  @Override public void showIconPropertiesError(String errors) {
    GenericDialogs.createGenericOkMessage(getActivity(),
        getString(R.string.image_requirements_error_popup_title), errors)
        .compose(bindUntilEvent(LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  @Override protected void setImageRealPath(String filePath) {
    currentModel.setStoreImagePath(filePath);
  }

  private void loadImageFromGallery() {
    requestAccessToExternalFileSystem(false, R.string.access_to_open_gallery_rationale, () -> {
      dispatchOpenGalleryIntent();
    }, () -> {
      Logger.e(TAG, "User denied access to camera");
    });
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    loadExtras(savedInstanceState);
  }

  @Override public void selectedGallery() {
    loadImageFromGallery();
  }

  @Override public void selectedCamera() {
    loadImageFromCamera();
  }
}
