package cm.aptoide.pt.v8engine.view.account.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.account.AccountErrorMapper;
import cm.aptoide.pt.v8engine.view.account.ImageLoaderFragment;
import cm.aptoide.pt.v8engine.view.dialog.ImageSourceSelectionDialogFragment;
import com.jakewharton.rxbinding.view.RxView;
import org.parceler.Parcel;
import org.parceler.Parcels;
import rx.Completable;
import rx.Observable;

public class ManageUserFragment extends ImageLoaderFragment
    implements ManageUserView, ImageSourceSelectionDialogFragment.ImageSourceSelectionHandler {

  public static final String EXTRA_USER_MODEL = "user_model";
  private static final String TAG = ManageUserFragment.class.getName();
  private ImageView userPicture;
  private RelativeLayout userPictureLayout;
  private EditText userName;
  private Button createUserButton;
  private ProgressDialog uploadWaitDialog;
  private Button cancelUserProfile;
  private TextView header;
  private ViewModel viewModel;
  private Toolbar toolbar;

  public static ManageUserFragment newInstanceToEdit(String userName, String userImage) {
    return newInstance(userName, userImage, true);
  }

  public static ManageUserFragment newInstanceToCreate() {
    return newInstance("", "", false);
  }

  private static ManageUserFragment newInstance(String userName, String userImage,
      boolean editUser) {
    Bundle args = new Bundle();
    args.putParcelable(EXTRA_USER_MODEL,
        Parcels.wrap(new ViewModel(userName, userImage, editUser)));

    ManageUserFragment manageUserFragment = new ManageUserFragment();
    manageUserFragment.setArguments(args);
    return manageUserFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = Parcels.unwrap(getArguments().getParcelable(EXTRA_USER_MODEL));
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(EXTRA_USER_MODEL, Parcels.wrap(viewModel));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_manage_user, container, false);
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (savedInstanceState != null) {
      viewModel = Parcels.unwrap(savedInstanceState.getParcelable(EXTRA_USER_MODEL));
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    userPictureLayout = (RelativeLayout) view.findViewById(R.id.create_user_image_action);
    userName = (EditText) view.findViewById(R.id.create_user_username_inserted);
    createUserButton = (Button) view.findViewById(R.id.create_user_create_profile);
    cancelUserProfile = (Button) view.findViewById(R.id.create_user_cancel_button);
    userPicture = (ImageView) view.findViewById(R.id.create_user_image);
    header = (TextView) view.findViewById(R.id.create_user_header_textview);

    final Context context = getContext();
    uploadWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(context,
        context.getString(R.string.please_wait_upload));

    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    if (viewModel.isEditProfile()) {
      toolbar.setTitle(getString(R.string.edit_profile_title));
    } else {
      toolbar.setTitle(R.string.create_user_title);
    }

    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(false);
    actionBar.setTitle(toolbar.getTitle());

    if (viewModel.isEditProfile()) {
      createUserButton.setText(getString(R.string.edit_profile_save_button));
      cancelUserProfile.setVisibility(View.VISIBLE);
      header.setText(getString(R.string.edit_profile_header_message));
    }

    loadUserData();

    final Context applicationContext = context.getApplicationContext();
    AptoideAccountManager accountManager = ((V8Engine) applicationContext).getAccountManager();
    CreateUserErrorMapper errorMapper =
        new CreateUserErrorMapper(context, new AccountErrorMapper(context), getResources());
    ManageUserPresenter presenter =
        new ManageUserPresenter(this, CrashReport.getInstance(), accountManager, errorMapper,
            getFragmentNavigator());
    attachPresenter(presenter, null);

    super.onViewCreated(view, savedInstanceState);
  }

  @Override public void onDestroyView() {
    if (uploadWaitDialog != null && uploadWaitDialog.isShowing()) {
      uploadWaitDialog.dismiss();
    }
    super.onDestroyView();
  }

  private void loadUserData() {
    if (viewModel != null) {
      final String image = viewModel.getImage();
      if (!TextUtils.isEmpty(image)) {
        loadImage(Uri.parse(image.replace("50", "150")));
      }

      final String name = viewModel.getName();
      if (!TextUtils.isEmpty(name)) {
        userName.setText(name);
      }
    }
  }

  @Override public void loadImage(Uri imagePath) {
    ImageLoader.with(getActivity())
        .loadWithCircleTransform(imagePath, userPicture, false);
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
    viewModel = new ViewModel(viewModel.getName(), filePath, viewModel.isEditProfile());
  }

  @Override public void showLoadImageDialog() {
    DialogFragment dialogFragment = new ImageSourceSelectionDialogFragment();
    dialogFragment.setTargetFragment(this, 0);
    dialogFragment.show(getChildFragmentManager(), "imageSourceChooser");
  }

  @Override public Observable<ViewModel> saveUserDataButtonClick() {
    return RxView.clicks(createUserButton)
        .map(__ -> {

          // "clean" image
          String image = viewModel.getImage();
          if (image != null) {
            image = image.contains("http") ? "" : viewModel.getImage();
          }

          // update model and return it.
          viewModel = new ViewModel(userName.getText()
              .toString(), image, viewModel.isEditProfile());
          return viewModel;
        });
  }

  @Override public Observable<Void> selectUserImageClick() {
    return RxView.clicks(userPictureLayout);
  }

  @Override public Observable<Void> cancelButtonClick() {
    return RxView.clicks(cancelUserProfile);
  }

  @Override public void showProgressDialog() {
    hideKeyboard();
    uploadWaitDialog.show();
  }

  @Override public void dismissProgressDialog() {
    uploadWaitDialog.dismiss();
  }

  @Override public Completable showErrorMessage(String error) {
    return ShowMessage.asLongObservableSnack(createUserButton, error);
  }

  private void loadImageFromCamera() {
    requestAccessToCamera(() -> dispatchTakePictureIntent(),
        () -> Logger.e(TAG, "User denied access to camera"));
  }

  private void loadImageFromGallery() {
    requestAccessToExternalFileSystem(false, R.string.access_to_open_gallery_rationale,
        () -> dispatchOpenGalleryIntent(), () -> Logger.e(TAG, "User denied access to camera"));
  }

  @Override public void selectedGallery() {
    loadImageFromGallery();
  }

  @Override public void selectedCamera() {
    loadImageFromCamera();
  }

  @Parcel protected static class ViewModel {
    final String name;
    final String image;
    final boolean editProfile;

    public ViewModel() {
      name = "";
      image = "";
      editProfile = false;
    }

    private ViewModel(String name, String image, boolean editProfile) {
      this.name = name;
      this.image = image;
      this.editProfile = editProfile;
    }

    public String getName() {
      return name;
    }

    public String getImage() {
      return image;
    }

    public boolean isEditProfile() {
      return editProfile;
    }

    public boolean hasImage() {
      return !TextUtils.isEmpty(image);
    }
  }
}
