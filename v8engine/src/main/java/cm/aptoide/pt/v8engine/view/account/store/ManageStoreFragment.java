package cm.aptoide.pt.v8engine.view.account.store;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
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
import org.parceler.Parcel;
import org.parceler.Parcels;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ManageStoreFragment extends ImageLoaderFragment
    implements ManageStoreView, ImageSourceSelectionDialogFragment.ImageSourceSelectionHandler {

  private static final String TAG = ManageStoreFragment.class.getName();

  private static final String EXTRA_STORE_MODEL = "store_model";
  private static final String EXTRA_GO_TO_HOME = "go_to_home";
  @LayoutRes private static final int LAYOUT = R.layout.fragment_manage_store;

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

  public static ManageStoreFragment newInstance(ViewModel storeModel, boolean goToHome) {
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

  @Override public Observable<ViewModel> saveDataClick() {
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

  @Override public Completable showError(@StringRes int errorMessage) {
    return ShowMessage.asLongObservableSnack(this, errorMessage);
  }

  @Override public Completable showGenericError() {
    return ShowMessage.asLongObservableSnack(this, R.string.having_some_trouble);
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

  @Override public void hideKeyboard() {
    super.hideKeyboard();
  }

  @Override public void loadExtras(@Nullable Bundle args) {
    super.loadExtras(args);
    if (args != null) {
      try {
        currentModel = Parcels.unwrap(args.getParcelable(EXTRA_STORE_MODEL));
      } catch (NullPointerException ex) {
        currentModel = new ViewModel();
      }
      goToHome = args.getBoolean(EXTRA_GO_TO_HOME, true);
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
    themeSelectorAdapter =
        new ThemeSelectorViewAdapter(storeThemePublishRelay, StoreTheme.getThemesFromVersion(8));
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

    attachPresenter(new ManageStorePresenter(this, CrashReport.getInstance(), goToHome,
        ((V8Engine) getActivity().getApplicationContext()).getStoreManager(),
        getFragmentNavigator()), null);
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

  private ViewModel updateAndGetStoreModel() {
    currentModel = ViewModel.from(currentModel, storeName.getText()
        .toString(), storeDescription.getText()
        .toString());
    currentModel.setStoreThemeName(themeSelectorAdapter.getSelectedThemeName());
    return currentModel;
  }

  @Override public int getContentViewId() {
    return LAYOUT;
  }

  private void setupViewsDefaultDataUsingStore(ViewModel storeModel) {
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

  private String getViewTitle(ViewModel storeModel) {
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

  @Parcel public static class ViewModel {
    long storeId;
    String storeName;
    String storeDescription;
    String storeImagePath;
    String storeThemeName;
    boolean newAvatar;

    public ViewModel() {
      this.storeId = -1;
      this.storeName = "";
      this.storeDescription = "";
      this.storeImagePath = "";
      this.storeThemeName = "";
      this.newAvatar = false;
    }

    public ViewModel(long storeId, String storeThemeName, String storeName, String storeDescription,
        String storeImagePath) {
      this.storeId = storeId;
      this.storeName = storeName;
      this.storeDescription = storeDescription;
      this.storeImagePath = storeImagePath;
      this.storeThemeName = storeThemeName;
      this.newAvatar = false;
    }

    public static ViewModel from(ViewModel otherStoreModel, String storeName,
        String storeDescription) {

      // if current store name is empty we use the old one
      if (TextUtils.isEmpty(storeName)) {
        storeName = otherStoreModel.getStoreName();
      }

      // if current store description is empty we use the old one
      if (TextUtils.isEmpty(storeDescription)) {
        storeDescription = otherStoreModel.getStoreDescription();
      }

      ViewModel newModel =
          new ViewModel(otherStoreModel.getStoreId(), otherStoreModel.getStoreThemeName(),
              storeName, storeDescription, otherStoreModel.getStoreImagePath());

      // if previous model had a new image, set it in new model
      if (otherStoreModel.hasNewAvatar()) {
        newModel.setStoreImagePath(otherStoreModel.getStoreImagePath());
      }

      return newModel;
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

    public String getStoreImagePath() {
      return storeImagePath;
    }

    public void setStoreImagePath(String storeAvatarPath) {
      this.storeImagePath = storeAvatarPath;
      this.newAvatar = true;
    }

    public boolean hasNewAvatar() {
      return newAvatar;
    }

    public long getStoreId() {
      return storeId;
    }

    public String getStoreThemeName() {
      return storeThemeName;
    }

    public void setStoreThemeName(String storeTheme) {
      this.storeThemeName = storeTheme;
    }

    public boolean storeExists() {
      return storeId >= 0L;
    }
  }
}
