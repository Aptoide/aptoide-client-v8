package cm.aptoide.pt.v8engine.view.account.store;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.presenter.CompositePresenter;
import cm.aptoide.pt.v8engine.store.StoreTheme;
import cm.aptoide.pt.v8engine.view.BackButtonFragment;
import cm.aptoide.pt.v8engine.view.account.ImagePickerErrorHandler;
import cm.aptoide.pt.v8engine.view.account.ImagePickerNavigator;
import cm.aptoide.pt.v8engine.view.account.ImagePickerPresenter;
import cm.aptoide.pt.v8engine.view.account.ImageValidator;
import cm.aptoide.pt.v8engine.view.account.PhotoFileGenerator;
import cm.aptoide.pt.v8engine.view.account.UriToPathResolver;
import cm.aptoide.pt.v8engine.view.account.exception.InvalidImageException;
import cm.aptoide.pt.v8engine.view.custom.DividerItemDecoration;
import cm.aptoide.pt.v8engine.view.dialog.ImagePickerDialog;
import cm.aptoide.pt.v8engine.view.permission.AccountPermissionProvider;
import cm.aptoide.pt.v8engine.view.permission.PermissionProvider;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.Arrays;
import org.parceler.Parcel;
import org.parceler.Parcels;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ManageStoreFragment extends BackButtonFragment implements ManageStoreView {

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

  private ViewModel currentModel;
  private boolean goToHome;
  private Toolbar toolbar;
  private ImagePickerDialog dialogFragment;
  private ImagePickerErrorHandler imagePickerErrorHandler;
  private ManageStoreNavigator manageStoreNavigator;
  private ImageValidator imageValidator;
  private ImagePickerNavigator imagePickerNavigator;
  private UriToPathResolver uriToPathResolver;
  private CrashReport crashReport;
  private AccountPermissionProvider accountPermissionProvider;
  private StoreManager storeManager;
  private String packageName;
  private String fileProviderAuthority;
  private PhotoFileGenerator photoFileGenerator;

  public static ManageStoreFragment newInstance(ViewModel storeModel, boolean goToHome) {
    Bundle args = new Bundle();
    args.putParcelable(EXTRA_STORE_MODEL, Parcels.wrap(storeModel));
    args.putBoolean(EXTRA_GO_TO_HOME, goToHome);

    ManageStoreFragment fragment = new ManageStoreFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    currentModel = Parcels.unwrap(getArguments().getParcelable(EXTRA_STORE_MODEL));
    goToHome = getArguments().getBoolean(EXTRA_GO_TO_HOME, true);

    dialogFragment =
        new ImagePickerDialog.Builder(getContext()).setViewRes(ImagePickerDialog.LAYOUT)
            .setTitle(R.string.upload_dialog_title)
            .setNegativeButton(R.string.cancel)
            .setCameraButton(R.id.button_camera)
            .setGalleryButton(R.id.button_gallery)
            .build();

    imagePickerErrorHandler = new ImagePickerErrorHandler(getContext());
    accountPermissionProvider = new AccountPermissionProvider(((PermissionProvider) getActivity()));
    storeManager = ((V8Engine) getActivity().getApplicationContext()).getStoreManager();
    packageName = (getActivity().getApplicationContext()).getPackageName();
    fileProviderAuthority = Application.getConfiguration()
        .getAppId() + ".provider";
    photoFileGenerator = new PhotoFileGenerator(getActivity(),
        getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileProviderAuthority);
    crashReport = CrashReport.getInstance();
    uriToPathResolver = new UriToPathResolver(getActivity().getContentResolver());
    imagePickerNavigator = new ImagePickerNavigator(getActivityNavigator());
    imageValidator = new ImageValidator(ImageLoader.with(getActivity()), Schedulers.computation());
    manageStoreNavigator = new ManageStoreNavigator(getFragmentNavigator());
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(EXTRA_STORE_MODEL, Parcels.wrap(currentModel));
    outState.putBoolean(EXTRA_GO_TO_HOME, goToHome);
  }

  @Override public void hideKeyboard() {
    super.hideKeyboard();
  }

  /**
   * @param pictureUri Load image to UI and save image in model to handle configuration changes.
   */
  @Override public void loadImage(String pictureUri) {
    loadImageStateless(pictureUri);
    currentModel.setNewAvatar(true);
  }

  @Override public Observable<DialogInterface> dialogCameraSelected() {
    return dialogFragment.cameraSelected();
  }

  @Override public Observable<DialogInterface> dialogGallerySelected() {
    return dialogFragment.gallerySelected();
  }

  @Override public void showImagePickerDialog() {
    dialogFragment.show();
  }

  @Override public void showIconPropertiesError(InvalidImageException exception) {
    imagePickerErrorHandler.showIconPropertiesError(exception)
        .compose(bindUntilEvent(LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  @Override public Observable<Void> selectStoreImageClick() {
    return RxView.clicks(selectStoreImageButton);
  }

  @Override public void dismissLoadImageDialog() {
    dialogFragment.dismiss();
  }

  @Override public void loadImageStateless(String pictureUri) {
    ImageLoader.with(getActivity())
        .loadUsingCircleTransformAndPlaceholder(pictureUri, storeImage,
            R.drawable.create_store_avatar);
    currentModel.setPictureUri(pictureUri);
  }

  @Override public Observable<ViewModel> saveDataClick() {
    return RxView.clicks(saveDataButton)
        .map(__ -> updateAndGetStoreModel());
  }

  @Override public Observable<Void> cancelClick() {
    return RxView.clicks(cancelChangesButton);
  }

  @Override public Completable showError(@StringRes int errorMessage) {
    return ShowMessage.asLongObservableSnack(this, errorMessage);
  }

  @Override public Completable showGenericError() {
    return ShowMessage.asLongObservableSnack(this, R.string.all_message_general_error);
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

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_manage_store, container, false);
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (savedInstanceState != null) {
      try {
        currentModel = Parcels.unwrap(savedInstanceState.getParcelable(EXTRA_STORE_MODEL));
      } catch (NullPointerException ex) {
        currentModel = new ViewModel();
      }
      goToHome = savedInstanceState.getBoolean(EXTRA_GO_TO_HOME, true);
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    bindViews(view);
    setupToolbarTitle();
    setupThemeSelector();
    setupViewsDefaultDataUsingCurrentModel();
    attachPresenters();
  }

  @Override public void onDestroyView() {
    dismissWaitProgressBar();
    if (dialogFragment != null) {
      dialogFragment.dismiss();
      dialogFragment = null;
    }
    super.onDestroyView();
  }

  private void attachPresenters() {
    final ImagePickerPresenter imagePickerPresenter =
        new ImagePickerPresenter(this, crashReport, accountPermissionProvider, photoFileGenerator,
            imageValidator, AndroidSchedulers.mainThread(), uriToPathResolver, imagePickerNavigator,
            getActivity().getContentResolver(), ImageLoader.with(getContext()));

    final ManageStorePresenter presenter =
        new ManageStorePresenter(this, crashReport, storeManager, getResources(), uriToPathResolver,
            packageName, manageStoreNavigator, goToHome);

    attachPresenter(new CompositePresenter(Arrays.asList(imagePickerPresenter, presenter)), null);
  }

  public void setupThemeSelector() {

    themeSelectorView.setLayoutManager(
        new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    PublishRelay<StoreTheme> storeThemePublishRelay = PublishRelay.create();
    themeSelectorAdapter =
        new ThemeSelectorViewAdapter(storeThemePublishRelay, StoreTheme.getThemesFromVersion(8));
    themeSelectorView.setAdapter(themeSelectorAdapter);

    themeSelectorAdapter.storeThemeSelection()
        .doOnNext(storeTheme -> currentModel.setStoreTheme(storeTheme))
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe();

    themeSelectorView.addItemDecoration(new DividerItemDecoration(getContext(), 8,
        DividerItemDecoration.LEFT | DividerItemDecoration.RIGHT));

    themeSelectorAdapter.selectTheme(currentModel.getStoreTheme());
  }

  public void setupToolbarTitle() {
    toolbar.setTitle(getViewTitle(currentModel));

    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(false);
    actionBar.setTitle(toolbar.getTitle());
  }

  public void bindViews(View view) {
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
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
  }

  private ViewModel updateAndGetStoreModel() {
    currentModel = ViewModel.update(currentModel, storeName.getText()
        .toString(), storeDescription.getText()
        .toString());
    currentModel.setStoreTheme(themeSelectorAdapter.getSelectedTheme());
    return currentModel;
  }

  private void setupViewsDefaultDataUsingCurrentModel() {

    storeName.setText(currentModel.getStoreName());
    storeDescription.setText(currentModel.getStoreDescription());

    if (!currentModel.storeExists()) {
      String appName = getString(R.string.app_name);
      header.setText(
          AptoideUtils.StringU.getFormattedString(R.string.create_store_header, getResources(),
              appName));
      chooseStoreNameTitle.setText(
          AptoideUtils.StringU.getFormattedString(R.string.create_store_name, getResources(),
              appName));
    } else {
      header.setText(R.string.edit_store_header);
      chooseStoreNameTitle.setText(
          AptoideUtils.StringU.getFormattedString(R.string.create_store_description_title,
              getResources()));
      storeName.setVisibility(View.GONE);
      storeDescription.setVisibility(View.VISIBLE);
      loadImageStateless(currentModel.getPictureUri());

      saveDataButton.setText(R.string.save_edit_store);
      cancelChangesButton.setText(R.string.cancel);
    }
  }

  private String getViewTitle(ViewModel storeModel) {
    if (!storeModel.storeExists()) {
      return getString(R.string.create_store_title);
    } else {
      return getString(R.string.edit_store_title);
    }
  }

  @Parcel public static class ViewModel {
    long storeId;
    String storeName;
    String storeDescription;
    String pictureUri;
    StoreTheme storeTheme;
    boolean newAvatar;

    public ViewModel() {
      this.storeId = -1;
      this.storeName = "";
      this.storeDescription = "";
      this.pictureUri = "";
      this.storeTheme = StoreTheme.DEFAULT;
      this.newAvatar = false;
    }

    public ViewModel(long storeId, StoreTheme storeTheme, String storeName, String storeDescription,
        String pictureUri) {
      this.storeId = storeId;
      this.storeName = storeName;
      this.storeDescription = storeDescription;
      this.pictureUri = pictureUri;
      this.storeTheme = storeTheme;
      this.newAvatar = false;
    }

    public static ViewModel update(ViewModel model, String storeName, String storeDescription) {

      // if current store name is empty we use the old one
      if (!TextUtils.isEmpty(storeName)) {
        model.setStoreName(storeName);
      }

      // if current store description is empty we use the old one
      if (!TextUtils.isEmpty(storeDescription)) {
        model.setStoreDescription(storeDescription);
      }

      return model;
    }

    public void setNewAvatar(boolean newAvatar) {
      this.newAvatar = newAvatar;
    }

    public String getStoreName() {
      return storeName;
    }

    public void setStoreName(String storeName) {
      this.storeName = storeName;
    }

    public String getStoreDescription() {
      return storeDescription;
    }

    public void setStoreDescription(String storeDescription) {
      this.storeDescription = storeDescription;
    }

    public String getPictureUri() {
      return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
      this.pictureUri = pictureUri;
    }

    public boolean hasNewAvatar() {
      return newAvatar;
    }

    public long getStoreId() {
      return storeId;
    }

    public void setStoreId(long storeId) {
      this.storeId = storeId;
    }

    public StoreTheme getStoreTheme() {
      return storeTheme;
    }

    public void setStoreTheme(StoreTheme storeTheme) {
      this.storeTheme = storeTheme;
    }

    public boolean storeExists() {
      return storeId >= 0L;
    }
  }
}
