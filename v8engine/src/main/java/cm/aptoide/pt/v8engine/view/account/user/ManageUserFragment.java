package cm.aptoide.pt.v8engine.view.account.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.presenter.CompositePresenter;
import cm.aptoide.pt.v8engine.view.BackButtonFragment;
import cm.aptoide.pt.v8engine.view.account.AccountErrorMapper;
import cm.aptoide.pt.v8engine.view.account.ImagePickerErrorHandler;
import cm.aptoide.pt.v8engine.view.account.ImagePickerNavigator;
import cm.aptoide.pt.v8engine.view.account.ImagePickerPresenter;
import cm.aptoide.pt.v8engine.view.account.ImageValidator;
import cm.aptoide.pt.v8engine.view.account.PhotoFileGenerator;
import cm.aptoide.pt.v8engine.view.account.UriToPathResolver;
import cm.aptoide.pt.v8engine.view.account.exception.InvalidImageException;
import cm.aptoide.pt.v8engine.view.dialog.ImagePickerDialog;
import cm.aptoide.pt.v8engine.view.permission.AccountPermissionProvider;
import cm.aptoide.pt.v8engine.view.permission.PermissionProvider;
import com.jakewharton.rxbinding.support.design.widget.RxSnackbar;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Arrays;
import org.parceler.Parcel;
import org.parceler.Parcels;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ManageUserFragment extends BackButtonFragment implements ManageUserView {

  private static final String EXTRA_USER_MODEL = "user_model";
  private static final String EXTRA_IS_EDIT = "is_edit";
  @DrawableRes private static final int DEFAULT_IMAGE_PLACEHOLDER = R.drawable.create_user_avatar;

  private ImageView userPicture;
  private RelativeLayout userPictureLayout;
  private EditText userName;
  private Button createUserButton;
  private ProgressDialog uploadWaitDialog;
  private Button cancelUserProfile;
  private TextView header;
  private ViewModel currentModel;
  private boolean isEditProfile;
  private Toolbar toolbar;
  private ImagePickerDialog dialogFragment;
  private ImagePickerErrorHandler imagePickerErrorHandler;
  private ManageUserNavigator navigator;
  private String fileProviderAuthority;
  private PhotoFileGenerator photoFileGenerator;
  private CrashReport crashReport;
  private UriToPathResolver uriToPathResolver;
  private AccountPermissionProvider accountPermissionProvider;
  private ImageValidator imageValidator;
  private ImagePickerNavigator imagePickerNavigator;
  private AptoideAccountManager accountManager;
  private CreateUserErrorMapper errorMapper;

  public static ManageUserFragment newInstanceToEdit() {
    return newInstance(true);
  }

  public static ManageUserFragment newInstanceToCreate() {
    return newInstance(false);
  }

  private static ManageUserFragment newInstance(boolean editUser) {
    Bundle args = new Bundle();
    args.putBoolean(EXTRA_IS_EDIT, editUser);

    ManageUserFragment manageUserFragment = new ManageUserFragment();
    manageUserFragment.setArguments(args);
    return manageUserFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Context context = getContext();
    if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_USER_MODEL)) {
      currentModel = Parcels.unwrap(savedInstanceState.getParcelable(EXTRA_USER_MODEL));
    } else {
      currentModel = new ViewModel();
    }

    Bundle args = getArguments();
    isEditProfile = args != null && args.getBoolean(EXTRA_IS_EDIT, false);

    navigator = new ManageUserNavigator(getFragmentNavigator());
    fileProviderAuthority = Application.getConfiguration()
        .getAppId() + ".provider";
    photoFileGenerator = new PhotoFileGenerator(getActivity(),
        getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileProviderAuthority);
    crashReport = CrashReport.getInstance();
    uriToPathResolver = new UriToPathResolver(getActivity().getContentResolver());
    accountPermissionProvider = new AccountPermissionProvider(((PermissionProvider) getActivity()));
    imageValidator = new ImageValidator(ImageLoader.with(context), Schedulers.computation());
    imagePickerNavigator = new ImagePickerNavigator(getActivityNavigator());
    accountManager = ((V8Engine) getActivity().getApplication()).getAccountManager();
    errorMapper =
        new CreateUserErrorMapper(context, new AccountErrorMapper(context), getResources());

    imagePickerErrorHandler = new ImagePickerErrorHandler(context);

    dialogFragment =
        new ImagePickerDialog.Builder(getContext()).setViewRes(ImagePickerDialog.LAYOUT)
            .setTitle(R.string.upload_dialog_title)
            .setNegativeButton(R.string.cancel)
            .setCameraButton(R.id.button_camera)
            .setGalleryButton(R.id.button_gallery)
            .build();

    uploadWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(context,
        context.getString(R.string.please_wait_upload));
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(EXTRA_USER_MODEL, Parcels.wrap(currentModel));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_manage_user, container, false);
  }

  private void bindViews(View view) {
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    userPictureLayout = (RelativeLayout) view.findViewById(R.id.create_user_image_action);
    userName = (EditText) view.findViewById(R.id.create_user_username_inserted);
    createUserButton = (Button) view.findViewById(R.id.create_user_create_profile);
    cancelUserProfile = (Button) view.findViewById(R.id.create_user_cancel_button);
    userPicture = (ImageView) view.findViewById(R.id.create_user_image);
    header = (TextView) view.findViewById(R.id.create_user_header_textview);
  }

  private void setupToolbar() {
    if (isEditProfile) {
      toolbar.setTitle(getString(R.string.edit_profile_title));
    } else {
      toolbar.setTitle(R.string.create_user_title);
    }
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(false);
    actionBar.setTitle(toolbar.getTitle());
  }

  private void attachPresenters() {
    final ImagePickerPresenter imagePickerPresenter =
        new ImagePickerPresenter(this, crashReport, accountPermissionProvider, photoFileGenerator,
            imageValidator, AndroidSchedulers.mainThread(), uriToPathResolver, imagePickerNavigator,
            getActivity().getContentResolver(), ImageLoader.with(getContext()));

    final ManageUserPresenter manageUserPresenter =
        new ManageUserPresenter(this, crashReport, accountManager, errorMapper, navigator,
            currentModel, isEditProfile, uriToPathResolver);

    attachPresenter(
        new CompositePresenter(Arrays.asList(manageUserPresenter, imagePickerPresenter)), null);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    bindViews(view);
    setupToolbar();
    if (isEditProfile) {
      createUserButton.setText(getString(R.string.edit_profile_save_button));
      cancelUserProfile.setVisibility(View.VISIBLE);
      header.setText(getString(R.string.edit_profile_header_message));
    }
    attachPresenters();
  }

  @Override public void onDestroyView() {
    if (uploadWaitDialog != null && uploadWaitDialog.isShowing()) {
      uploadWaitDialog.dismiss();
    }
    super.onDestroyView();
  }

  @Override public void setUserName(String name) {
    currentModel.setName(name);
    userName.setText(name);
  }

  @Override public Observable<ViewModel> saveUserDataButtonClick() {
    return RxView.clicks(createUserButton)
        .map(__ -> updateModelAndGet());
  }

  @Override public Observable<Void> cancelButtonClick() {
    return RxView.clicks(cancelUserProfile);
  }

  @Override public void showProgressDialog() {
    hideKeyboard();
    uploadWaitDialog.show();
  }

  @Override public void hideProgressDialog() {
    uploadWaitDialog.dismiss();
  }

  @Override public Completable showErrorMessage(String error) {
    return Single.fromCallable(() -> Snackbar.make(createUserButton, error, Snackbar.LENGTH_LONG))
        .flatMapCompletable(snackbar -> {
          snackbar.show();
          return RxSnackbar.dismisses(snackbar)
              .toCompletable();
        });
  }

  @Override public void loadImageStateless(String pictureUri) {
    currentModel.setPictureUri(pictureUri);
    ImageLoader.with(getActivity())
        .loadUsingCircleTransformAndPlaceholder(pictureUri, userPicture, DEFAULT_IMAGE_PLACEHOLDER);
  }

  /**
   * @param pictureUri Load image to UI and save image in model to handle configuration changes.
   */
  @Override public void loadImage(String pictureUri) {
    loadImageStateless(pictureUri);
    currentModel.setNewPicture(true);
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
    return RxView.clicks(userPictureLayout);
  }

  @Override public void dismissLoadImageDialog() {
    dialogFragment.dismiss();
  }

  @Nullable public ViewModel updateModelAndGet() {
    return ViewModel.from(currentModel, userName.getText()
        .toString());
  }

  @Parcel protected static class ViewModel {
    String name;
    String pictureUri;
    boolean hasNewPicture;

    public ViewModel() {
      name = "";
      pictureUri = "";
      hasNewPicture = false;
    }

    public ViewModel(String name, String pictureUri) {
      this.name = name;
      this.pictureUri = pictureUri;
      this.hasNewPicture = false;
    }

    public static ViewModel from(ViewModel otherModel, String otherName) {
      otherModel.setName(otherName);
      return otherModel;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPictureUri() {
      return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
      this.pictureUri = pictureUri;
    }

    public boolean hasData() {
      return !TextUtils.isEmpty(getName()) || !TextUtils.isEmpty(getPictureUri());
    }

    public void setNewPicture(boolean hasNewPicture) {
      this.hasNewPicture = hasNewPicture;
    }

    public boolean hasNewPicture() {
      return hasNewPicture;
    }
  }
}
