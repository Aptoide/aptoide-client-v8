package cm.aptoide.pt.account.view.store;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.ImagePickerErrorHandler;
import cm.aptoide.pt.account.view.ImagePickerPresenter;
import cm.aptoide.pt.account.view.exception.InvalidImageException;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.presenter.CompositePresenter;
import cm.aptoide.pt.themes.StoreTheme;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.BackButtonFragment;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.custom.DividerItemDecoration;
import cm.aptoide.pt.view.dialog.ImagePickerDialog;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.Arrays;
import javax.inject.Inject;
import org.parceler.Parcels;
import rx.Observable;

public class ManageStoreFragment extends BackButtonFragment
    implements ManageStoreView, NotBottomNavigationView {

  private static final String EXTRA_STORE_MODEL = "store_model";
  private static final String EXTRA_GO_TO_HOME = "go_to_home";
  private static final float STROKE_SIZE = 0.040f;
  private static final float SPACE_BETWEEN = 0.0f;
  @Inject ImagePickerPresenter imagePickerPresenter;
  @Inject ManageStorePresenter manageStorePresenter;
  @Inject ScreenOrientationManager orientationManager;
  @Inject ThemeManager themeManager;
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
  private Toolbar toolbar;
  private ImagePickerDialog dialogFragment;
  private ImagePickerErrorHandler imagePickerErrorHandler;

  public static ManageStoreFragment newInstance(ManageStoreViewModel storeModel, boolean goToHome) {
    Bundle args = new Bundle();
    args.putParcelable(EXTRA_STORE_MODEL, Parcels.wrap(storeModel));
    args.putBoolean(EXTRA_GO_TO_HOME, goToHome);

    ManageStoreFragment fragment = new ManageStoreFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    currentModel = Parcels.unwrap(getArguments().getParcelable(EXTRA_STORE_MODEL));
    goToHome = getArguments().getBoolean(EXTRA_GO_TO_HOME, true);
    dialogFragment = new ImagePickerDialog.Builder(new ContextThemeWrapper(getContext(),
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId),
        themeManager).setViewRes(ImagePickerDialog.LAYOUT)
        .setTitle(R.string.upload_dialog_title)
        .setNegativeButton(R.string.cancel)
        .setCameraButton(R.id.button_camera)
        .setGalleryButton(R.id.button_gallery)
        .build();

    imagePickerErrorHandler = new ImagePickerErrorHandler(getContext(), themeManager);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    bindViews(view);
    setupToolbarTitle();
    setupThemeSelector();
    setupViewsDefaultDataUsingCurrentModel();

    attachPresenters();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
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
    int color = themeManager.getAttributeForTheme(R.attr.colorPrimary).data;
    ImageLoader.with(getActivity())
        .loadWithShadowCircleTransform(pictureUri, storeImage, color, SPACE_BETWEEN, STROKE_SIZE);
    currentModel.setPictureUri(pictureUri);
  }

  @Override public Observable<ManageStoreViewModel> saveDataClick() {
    return RxView.clicks(saveDataButton)
        .map(__ -> updateAndGetStoreModel())
        .doOnNext(__ -> hideKeyboard());
  }

  @Override public Observable<ManageStoreViewModel> cancelClick() {
    return RxView.clicks(cancelChangesButton)
        .map(__ -> currentModel)
        .doOnNext(__ -> hideKeyboard());
  }

  @Override public void showWaitProgressBar() {
    orientationManager.lock();
    if (waitDialog != null && !waitDialog.isShowing()) {
      waitDialog.show();
    }
  }

  @Override public void dismissWaitProgressBar() {
    orientationManager.unlock();
    if (waitDialog != null && waitDialog.isShowing()) {
      waitDialog.dismiss();
    }
  }

  @Override public void showError(String errorMessage) {
    Snackbar.make(saveDataButton, errorMessage, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showSuccessMessage() {
    Snackbar.make(saveDataButton, getString(R.string.done), Snackbar.LENGTH_LONG)
        .show();
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
        currentModel = new ManageStoreViewModel();
      }
      goToHome = savedInstanceState.getBoolean(EXTRA_GO_TO_HOME, true);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(EXTRA_STORE_MODEL, Parcels.wrap(currentModel));
    outState.putBoolean(EXTRA_GO_TO_HOME, goToHome);
  }

  @Override public void onDestroyView() {
    dismissWaitProgressBar();

    if (dialogFragment != null) {
      dialogFragment.dismiss();
      dialogFragment = null;
    }

    hideKeyboard();

    super.onDestroyView();
  }

  @Override public void hideKeyboard() {
    super.hideKeyboard();
  }

  private void attachPresenters() {

    attachPresenter(
        new CompositePresenter(Arrays.asList(imagePickerPresenter, manageStorePresenter)));
  }

  public void setupThemeSelector() {

    themeSelectorView.setLayoutManager(
        new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
    PublishRelay<StoreTheme> storeThemePublishRelay = PublishRelay.create();
    themeSelectorAdapter =
        new ThemeSelectorViewAdapter(storeThemePublishRelay, StoreTheme.getThemesFromVersion(8),
            themeManager);
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
    chooseStoreNameTitle = view.findViewById(R.id.create_store_choose_name_title);
    selectStoreImageButton = view.findViewById(R.id.create_store_image_action);
    storeImage = view.findViewById(R.id.create_store_image);
    storeName = view.findViewById(R.id.create_store_name);
    storeDescription = view.findViewById(R.id.edit_store_description);
    cancelChangesButton = view.findViewById(R.id.create_store_skip);
    saveDataButton = view.findViewById(R.id.create_store_action);
    themeSelectorView = view.findViewById(R.id.theme_selector);

    waitDialog = GenericDialogs.createGenericPleaseWaitDialog(getActivity(),
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId,
        getActivity().getApplicationContext()
            .getString(R.string.please_wait_upload));
    toolbar = view.findViewById(R.id.toolbar);
  }

  private ManageStoreViewModel updateAndGetStoreModel() {
    currentModel = ManageStoreViewModel.update(currentModel, storeName.getText()
        .toString(), storeDescription.getText()
        .toString());
    currentModel.setStoreTheme(themeSelectorAdapter.getSelectedTheme());
    return currentModel;
  }

  private void setupViewsDefaultDataUsingCurrentModel() {
    storeName.setText(currentModel.getStoreName());

    if (!currentModel.storeExists()) {
      chooseStoreNameTitle.setText(R.string.create_store_name);
    } else {
      chooseStoreNameTitle.setText(
          AptoideUtils.StringU.getFormattedString(R.string.description, getResources()));
      storeName.setVisibility(View.GONE);
      storeDescription.setVisibility(View.VISIBLE);
      storeDescription.setText(currentModel.getStoreDescription());
      loadImageStateless(currentModel.getPictureUri());

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
}
