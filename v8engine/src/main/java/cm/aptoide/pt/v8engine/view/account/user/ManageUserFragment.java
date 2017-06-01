package cm.aptoide.pt.v8engine.view.account.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.account.AccountErrorMapper;
import cm.aptoide.pt.v8engine.view.account.ImageLoaderFragment;
import cm.aptoide.pt.v8engine.view.dialog.ImageSourceSelectionDialogFragment;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
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

  @Override public void onDestroyView() {
    if (uploadWaitDialog != null && uploadWaitDialog.isShowing()) {
      uploadWaitDialog.dismiss();
    }
    super.onDestroyView();
  }

  @Override public void setupViews() {
    super.setupViews();

    if (viewModel != null && !TextUtils.isEmpty(viewModel.getImage())) {
      loadImage(Uri.parse(viewModel.getImage()));
    }

    if (viewModel.isEditProfile()) {
      createUserButton.setText(getString(R.string.edit_profile_save_button));
      String image = viewModel.getImage();
      if (image != null) {
        image = image.replace("50", "150");
        loadImage(Uri.parse(image));
      }
      if (viewModel.getName() != null) {
        userName.setText(viewModel.getName());
      }
      cancelUserProfile.setVisibility(View.VISIBLE);
      header.setText(getString(R.string.edit_profile_header_message));
    }

    final Context context = getContext();
    final Context applicationContext = context.getApplicationContext();
    AptoideAccountManager accountManager = ((V8Engine) applicationContext).getAccountManager();
    CreateUserErrorMapper errorMapper =
        new CreateUserErrorMapper(context, new AccountErrorMapper(context));
    ManageUserPresenter presenter =
        new ManageUserPresenter(this, CrashReport.getInstance(), accountManager, errorMapper);
    attachPresenter(presenter, null);

    final Completable dismissProgressDialogCompletable =
        Completable.fromAction(() -> dismissProgressDialog());
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    super.setupToolbarDetails(toolbar);
    if (viewModel.isEditProfile()) {
      toolbar.setTitle(getString(R.string.edit_profile_title));
    } else {
      toolbar.setTitle(R.string.create_user_title);
    }
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    userPictureLayout = (RelativeLayout) view.findViewById(R.id.create_user_image_action);
    userName = (EditText) view.findViewById(R.id.create_user_username_inserted);
    createUserButton = (Button) view.findViewById(R.id.create_user_create_profile);
    cancelUserProfile = (Button) view.findViewById(R.id.create_user_cancel_button);
    userPicture = (ImageView) view.findViewById(R.id.create_user_image);
    header = (TextView) view.findViewById(R.id.create_user_header_textview);

    final Context context = getContext();
    uploadWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(context,
        context.getString(R.string.please_wait_upload));
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    viewModel = Parcels.unwrap(args.getParcelable(EXTRA_USER_MODEL));
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(EXTRA_USER_MODEL, Parcels.wrap(viewModel));
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_create_user;
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

  @Override public void navigateBack() {
    getFragmentNavigator().popBackStack();
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

  @Override public void navigateToProfileStepOne() {
    final FragmentNavigator fragmentNavigator = getFragmentNavigator();
    fragmentNavigator.cleanBackStack();
    fragmentNavigator.navigateTo(ProfileStepOneFragment.newInstance());
  }

  @Override public void navigateToHome() {
    getFragmentNavigator().navigateToHomeCleaningBackStack();
  }

  @Override public void showProgressDialog() {
    hideKeyboard();
    uploadWaitDialog.show();
  }

  @Override public void dismissProgressDialog() {
    uploadWaitDialog.dismiss();
  }

  @Override public Completable showErrorMessage(String error) {
    return ShowMessage.asObservableSnack(createUserButton, error)
        .toCompletable();
  }

  private void loadImageFromCamera() {
    requestAccessToCamera(() -> {
      dispatchTakePictureIntent();
    }, () -> {
      Logger.e(TAG, "User denied access to camera");
    });
  }

  private void loadImageFromGallery() {
    requestAccessToExternalFileSystem(false, R.string.access_to_open_gallery_rationale, () -> {
      dispatchOpenGalleryIntent();
    }, () -> {
      Logger.e(TAG, "User denied access to camera");
    });
  }

  @Override public void selectedGallery() {
    loadImageFromGallery();
  }

  @Override public void selectedCamera() {
    loadImageFromCamera();
  }

  @Parcel protected static class ViewModel {
    private final String name;
    private final String image;
    private final boolean editProfile;

    public ViewModel(){
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
