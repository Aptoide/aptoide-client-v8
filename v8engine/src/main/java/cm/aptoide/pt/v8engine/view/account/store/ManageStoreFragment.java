package cm.aptoide.pt.v8engine.view.account.store;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import cm.aptoide.pt.v8engine.networking.StoreBodyInterceptor;
import cm.aptoide.pt.v8engine.store.StoreTheme;
import cm.aptoide.pt.v8engine.view.account.PictureLoaderFragment;
import cm.aptoide.pt.v8engine.view.custom.DividerItemDecoration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import com.trello.rxlifecycle.android.FragmentEvent;
import okhttp3.OkHttpClient;
import org.parceler.Parcels;
import retrofit2.Converter;
import rx.Observable;

public class ManageStoreFragment extends PictureLoaderFragment
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

  private ManageStoreViewModel updateAndGetStoreModel() {
    currentModel =
        ManageStoreViewModel.from(currentModel, storeName.toString(), storeDescription.toString());
    currentModel.setStoreThemeName(themeSelectorAdapter.getSelectedThemeName());
    return currentModel;
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_manage_store;
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
    attachPresenter(buildPresenter(engine, currentModel, goToHome), null);
  }

  @Override protected boolean hasToolbar() {
    return true;
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    super.setupToolbarDetails(toolbar);
    toolbar.setTitle(getViewTitle(currentModel));
  }

  @Override public void bindViews(@Nullable View view) {
    header = (TextView) view.findViewById(R.id.create_store_header);
    chooseStoreNameTitle = (TextView) view.findViewById(R.id.create_store_choose_name_title);
    selectStoreImageButton = view.findViewById(R.id.create_store_image_action);
    storeImage = (ImageView) view.findViewById(R.id.create_store_image);
    storeName = (EditText) view.findViewById(R.id.create_store_name);
    storeDescription = (EditText) view.findViewById(R.id.edit_store_description);
    cancelChangesButton = (Button) view.findViewById(R.id.create_store_skip);
    saveDataButton = (Button) view.findViewById(R.id.create_store_action);
    themeSelectorView = (RecyclerView) view.findViewById(R.id.theme_selector);
  }

  @Override public void loadExtras(@Nullable Bundle args) {
    if (args != null) {
      super.loadExtras(args);
      try {
        currentModel = Parcels.unwrap(args.getParcelable(EXTRA_STORE_MODEL));
      } catch (NullPointerException ex) {
        currentModel = new ManageStoreViewModel();
      }
      goToHome = args.getBoolean(EXTRA_GO_TO_HOME, true);
    }
  }

  @NonNull private ManageStorePresenter buildPresenter(@NonNull V8Engine engine,
      @NonNull ManageStoreViewModel storeModel, boolean goToHome) {
    AptoideAccountManager accountManager = engine.getAccountManager();
    BodyInterceptor<BaseBody> bodyInterceptorV3 = engine.getBaseBodyInterceptorV3();
    IdsRepository idsRepository = engine.getIdsRepository();
    RequestBodyFactory requestBodyFactory = new RequestBodyFactory();
    OkHttpClient httpClient = engine.getDefaultClient();
    Converter.Factory converterFactory = WebService.getDefaultConverter();

    final StoreBodyInterceptor storeBodyInterceptor =
        buildStoreInterceptor(storeModel, accountManager, idsRepository, requestBodyFactory);

    StoreManagerFactory storeManagerFactory =
        new StoreManagerFactory(accountManager, httpClient, converterFactory, storeBodyInterceptor,
            bodyInterceptorV3);
    return new ManageStorePresenter(this, goToHome, storeManagerFactory.create());
  }

  @NonNull private StoreBodyInterceptor buildStoreInterceptor(ManageStoreViewModel storeModel,
      AptoideAccountManager accountManager, IdsRepository idsRepository,
      RequestBodyFactory requestBodyFactory) {

    ObjectMapper serializer = new ObjectMapper();
    serializer.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    return new StoreBodyInterceptor(idsRepository.getUniqueIdentifier(), accountManager,
        requestBodyFactory, storeModel.getStoreThemeName(), storeModel.getStoreDescription(),
        serializer);
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
      loadImage(Uri.parse(storeModel.getStoreImagePath()));
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

  public void loadImageFromCamera() {
    requestAccessToCamera(() -> {
      dispatchTakePictureIntent();
    }, () -> {
      Logger.e(TAG, "User denied access to camera");
    });
  }

  @Override public void loadImage(Uri imagePath) {
    ImageLoader.with(getActivity())
        .loadUsingCircleTransform(imagePath, storeImage);
    currentModel.setStoreImagePath(imagePath.toString());
  }

  @Override public void showIconPropertiesError(String errors) {
    GenericDialogs.createGenericOkMessage(getActivity(),
        getString(R.string.image_requirements_error_popup_title), errors)
        .compose(bindUntilEvent(LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  public void loadImageFromGallery() {
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
